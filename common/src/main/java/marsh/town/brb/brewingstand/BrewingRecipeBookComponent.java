package marsh.town.brb.brewingstand;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.enums.BRBRecipeBookType;
import marsh.town.brb.generic.GenericRecipeBookComponent;
import marsh.town.brb.interfaces.IPinningComponent;
import marsh.town.brb.recipe.BRBRecipeBookCategories;
import marsh.town.brb.util.BRBTextures;
import marsh.town.brb.util.BrewingGhostRecipe;
import marsh.town.brb.util.ClientInventoryUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getFrom;
import static marsh.town.brb.brewingstand.PlatformPotionUtil.getIngredient;

@Environment(EnvType.CLIENT)
public class BrewingRecipeBookComponent extends GenericRecipeBookComponent<BrewingStandMenu> implements IPinningComponent<BrewableResult> {
    BrewingClientRecipeBook book;
    public final BrewingGhostRecipe ghostRecipe = new BrewingGhostRecipe();
    public final BrewingRecipeBookResults recipesArea = new BrewingRecipeBookResults();
    private final List<BrewableRecipeGroupButtonWidget> tabButtons = Lists.newArrayList();
    @Nullable
    public BrewableRecipeGroupButtonWidget currentTab;
    private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("brb.gui.togglePotions.brewable");
    boolean doubleRefresh = true;

    public void initialize(int parentWidth, int parentHeight, Minecraft client, boolean narrow, BrewingStandMenu brewingStandScreenHandler) {
        super.init(parentWidth, parentHeight, client, narrow, brewingStandScreenHandler);

        this.book = new BrewingClientRecipeBook();
        this.setVisible(BetterRecipeBook.rememberedBrewingOpen);
        // this.cachedInvChangeCount = client.player.getInventory().getChangeCount();
        this.initVisuals();

        // this code is responsible for selectively rendering ghost slots
        ghostRecipe.setRenderingPredicate((type, ingredient) -> {
            ItemStack real = brewingStandScreenHandler.slots.get(ingredient.getContainerSlot()).getItem();
            switch (type) {
                case ITEM:
                case BACKGROUND:
                    // slot 0 is the preview so map it to 1
                    ItemStack fake = ingredient.getContainerSlot() == 0 ? ingredient.getOwner().getBySlot(1).getItem() : ingredient.getItem();

                    // if the ingredient is in one of the output slots
                    if (ingredient.getContainerSlot() < 3) {
                        if (real.getItem() instanceof PotionItem) {
                            Potion realPotion = PotionUtils.getPotion(real);
                            Potion fakePotion = PotionUtils.getPotion(fake);

                            return !realPotion.equals(fakePotion);
                        } else { // else it's not valid
                            return true;
                        }
                    } else { // else it's the consumable item
                        return !real.is(fake.getItem());
                    }
                case TOOLTIP:
                    return real.isEmpty();
                default:
                    return true;
            }
        });

        // still required?
        //client.keyboardHandler.setSendRepeatsToGui(true);
    }

    public ItemStack getInputStack(BrewableResult result) {
        Potion inputPotion = getFrom(result.recipe);
        Ingredient ingredient = getIngredient(result.recipe);
        ResourceLocation identifier = BuiltInRegistries.POTION.getKey(inputPotion);
        ItemStack inputStack;
        if (this.currentTab.getGroup() == BRBRecipeBookCategories.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (this.currentTab.getGroup() == BRBRecipeBookCategories.BREWING_LINGERING_POTION) {
            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateTag().putString("Potion", identifier.toString());
        return inputStack;
    }

    public void initVisuals() {
        super.initVisuals();

        int i = (this.width - 147) / 2 - this.xOffset;
        int j = (this.height - 166) / 2;

        this.recipesArea.initialize(this.minecraft, i, j, menu);
        this.tabButtons.clear();
        this.book.setFilteringCraftable(BetterRecipeBook.rememberedBrewingToggle);
        this.filterButton = new StateSwitchingButton(i + 110, j + 12, 26, 16, this.book.isFilteringCraftable());
        this.updateFilterButtonTooltip();
        this.setBookButtonTexture();

        for (BRBRecipeBookCategories brewingRecipeBookGroup : BRBRecipeBookCategories.getGroups(BRBRecipeBookType.BREWING)) {
            this.tabButtons.add(new BrewableRecipeGroupButtonWidget(brewingRecipeBookGroup));
        }

        if (this.currentTab != null) {
            this.currentTab = this.tabButtons.stream().filter((button) -> button.getGroup().equals(this.currentTab.getGroup())).findFirst().orElse(null);
        }

        if (this.currentTab == null) {
            this.currentTab = this.tabButtons.get(0);
        }

        this.currentTab.setStateTriggered(true);
        this.updateCollections(false);
        this.refreshTabButtons();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isVisible()) {
            if (this.recipesArea.mouseClicked(mouseX, mouseY, button)) {
                BrewableResult result = this.recipesArea.getCurrentClickedRecipe();
                if (result != null) {
                    this.ghostRecipe.clear();

                    if (!result.hasMaterials(this.currentTab.getGroup(), menu.slots)) {
                        showGhostRecipe(result, menu.slots);
                        return true;
                    }

                    ItemStack inputStack = getInputStack(result);
                    Ingredient ingredient = getIngredient(result.recipe);

                    int slotIndex = 0;
                    int usedInputSlots = 0;
                    for (Slot slot : menu.slots) {
                        ItemStack itemStack = slot.getItem();

                        assert inputStack.getTag() != null;
                        if (inputStack.getTag().equals(itemStack.getTag()) && inputStack.getItem().equals(itemStack.getItem())) {
                            if (usedInputSlots <= 2) {
                                assert Minecraft.getInstance().gameMode != null;
                                ClientInventoryUtil.storeItem(-1, i -> i > 4);
                                Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                                Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(usedInputSlots).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                                ClientInventoryUtil.storeItem(-1, i -> i > 4);
                                ++usedInputSlots;
                            }
                        } else if (ingredient.getItems()[0].getItem().equals(slot.getItem().getItem())) {
                            assert Minecraft.getInstance().gameMode != null;
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(3).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                        }

                        ++slotIndex;
                    }

                    this.updateCollections(false);
                }

                return true;
            } else {
                assert this.searchBox != null;
                if (this.searchBox.mouseClicked(mouseX, mouseY, button)) {
                    searchBox.setFocused(true);
                    ignoreTextInput = true;
                    return true;
                }
                searchBox.setFocused(false);
                ignoreTextInput = false;

                if (this.filterButton.mouseClicked(mouseX, mouseY, button)) {
                    boolean bl = this.toggleFiltering();
                    this.filterButton.setStateTriggered(bl);
                    this.updateFilterButtonTooltip();
                    BetterRecipeBook.rememberedBrewingToggle = bl;
                    this.updateCollections(false);
                    return true;
                } else if (this.settingsButtonMouseClicked(this.settingsButton, mouseX, mouseY, button)) {
                    return true;
                }

                Iterator<BrewableRecipeGroupButtonWidget> var6 = this.tabButtons.iterator();

                BrewableRecipeGroupButtonWidget brewableRecipeGroupButtonWidget;
                do {
                    if (!var6.hasNext()) {
                        return false;
                    }

                    brewableRecipeGroupButtonWidget = var6.next();
                } while (!brewableRecipeGroupButtonWidget.mouseClicked(mouseX, mouseY, button));

                if (this.currentTab != brewableRecipeGroupButtonWidget) {
                    if (this.currentTab != null) {
                        this.currentTab.setStateTriggered(false);
                    }

                    this.currentTab = brewableRecipeGroupButtonWidget;
                    this.currentTab.setStateTriggered(true);
                    this.updateCollections(true);
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public void showGhostRecipe(BrewableResult result, List<Slot> slots) {
        this.ghostRecipe.addIngredient(3, Ingredient.of(getIngredient(result.recipe).getItems()[0]), slots.get(3).x, slots.get(3).y);

        assert currentTab != null;
        ItemStack inputStack = result.inputAsItemStack(currentTab.getGroup());
        this.ghostRecipe.addIngredient(0, Ingredient.of(inputStack), slots.get(0).x, slots.get(0).y);
        this.ghostRecipe.addIngredient(1, Ingredient.of(inputStack), slots.get(1).x, slots.get(1).y);
        this.ghostRecipe.addIngredient(2, Ingredient.of(inputStack), slots.get(2).x, slots.get(2).y);
    }

    private boolean toggleFiltering() {
        boolean bl = !this.book.isFilteringCraftable();
        this.book.setFilteringCraftable(bl);
        BetterRecipeBook.rememberedBrewingToggle = bl;
        return bl;
    }

    @Override
    public void updateCollections(boolean resetCurrentPage) {
        if (this.currentTab == null) return;
        if (this.searchBox == null) return;

        // Create a copy to not mess with the original list
        List<BrewableResult> results = new ArrayList<>(book.getResultsForCategory(currentTab.getGroup()));

        String string = this.searchBox.getValue();
        if (!string.isEmpty()) {
            results.removeIf(itemStack -> !itemStack.ingredient.getHoverName().getString().toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)));
        }

        if (this.book.isFilteringCraftable()) {
            results.removeIf((result) -> !result.hasMaterials(currentTab.getGroup(), menu.slots));
        }

        this.betterRecipeBook$sortByPinsInPlace(results);

        this.recipesArea.setResults(results, resetCurrentPage, currentTab.getGroup());
    }

    private void refreshTabButtons() {
        int i = (this.width - 147) / 2 - this.xOffset - 30;
        int j = (this.height - 166) / 2 + 3;
        int l = 0;

        for (BrewableRecipeGroupButtonWidget brewableRecipeGroupButtonWidget : this.tabButtons) {
            BRBRecipeBookCategories categories = brewableRecipeGroupButtonWidget.getGroup();
            if (categories == BRBRecipeBookCategories.BREWING_SEARCH) {
                brewableRecipeGroupButtonWidget.visible = true;
            }
            brewableRecipeGroupButtonWidget.setPosition(i, j + 27 * l++);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) return;

        if (doubleRefresh) {
            // Minecraft doesn't populate the inventory on initialization so this is the only solution I have
            updateCollections(true);
            doubleRefresh = false;
        }

        gui.pose().pushPose();
        gui.pose().translate(0.0f, 0.0f, 100.0f);

        // blit recipe book background texture
        int blitX = (this.width - 147) / 2 - this.xOffset;
        int blitY = (this.height - 166) / 2;
        gui.blit(BRBTextures.RECIPE_BOOK_BACKGROUND_TEXTURE, blitX, blitY, 1, 1, 147, 166);

        // render search box
        this.searchBox.render(gui, mouseX, mouseY, delta);

        // render tab buttons
        for (BrewableRecipeGroupButtonWidget brewableRecipeGroupButtonWidget : this.tabButtons) {
            brewableRecipeGroupButtonWidget.render(gui, mouseX, mouseY, delta);
        }

        // render the toggle brewable filter button
        this.filterButton.render(gui, mouseX, mouseY, delta);

        this.renderSettingsButton(this.settingsButton, gui, mouseX, mouseY, delta);

        // render the recipe book page contents
        this.recipesArea.render(gui, blitX, blitY, mouseX, mouseY, delta);

        gui.pose().popPose();
    }

    public void renderGhostRecipe(GuiGraphics guiGraphics, int x, int y, boolean bl, float delta) {
        this.ghostRecipe.render(guiGraphics, this.minecraft, x, y, bl, delta);
    }

    public void toggleOpen() {
        this.setVisible(!this.isVisible());
    }

    protected void setBookButtonTexture() {
        this.filterButton.initTextureValues(BRBTextures.RECIPE_BOOK_FILTER_BUTTON_SPRITES);
    }

    public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
        if (this.isVisible()) {
            this.recipesArea.drawTooltip(gui, mouseX, mouseY);

            this.renderSettingsButtonTooltip(this.settingsButton, gui, mouseX, mouseY);

            ghostRecipe.renderTooltip(gui, x, y, mouseX, mouseY);
        }
    }

    @Override
    public Component getRecipeFilterName() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_F && BetterRecipeBook.config.enablePinning) {
            for (BrewableAnimatedResultButton resultButton : this.recipesArea.buttons) {
                if (resultButton.isHoveredOrFocused()) {
                    BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavouritePotion(resultButton.getRecipe().recipe);
                    this.updateCollections(false);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasClickedOutside(double d, double e, int i, int j, int k, int l, int m) {
        if (!this.isVisible()) {
            return true;
        }
        boolean bl = d < (double) i || e < (double) j || d >= (double) (i + k) || e >= (double) (j + l);
        boolean bl2 = (double) (i - 147) < d && d < (double) i && (double) j < e && e < (double) (j + l);
        return bl && !bl2 && !this.currentTab.isHoveredOrFocused();
    }

    @Override
    public void recipesShown(List<RecipeHolder<?>> list) {

    }

    public void recipesUpdated() {
        updateCollections(false);
    }

}
