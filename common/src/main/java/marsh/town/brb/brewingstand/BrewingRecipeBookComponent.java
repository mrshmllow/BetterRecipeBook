package marsh.town.brb.brewingstand;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.enums.BRBRecipeBookType;
import marsh.town.brb.generic.BRBGroupButtonWidget;
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
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getFrom;
import static marsh.town.brb.brewingstand.PlatformPotionUtil.getIngredient;

@Environment(EnvType.CLIENT)
public class BrewingRecipeBookComponent extends GenericRecipeBookComponent<BrewingStandMenu, BrewingRecipeBookResults, BrewingClientRecipeBook> implements IPinningComponent<BrewableResult> {
    public final BrewingGhostRecipe ghostRecipe = new BrewingGhostRecipe();
    private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("brb.gui.togglePotions.brewable");
    boolean doubleRefresh = true;

    public BrewingRecipeBookComponent() {
        super(BrewingClientRecipeBook::new);
    }

    public void initialize(int parentWidth, int parentHeight, Minecraft client, boolean narrow, BrewingStandMenu brewingStandScreenHandler) {
        super.init(parentWidth, parentHeight, client, narrow, brewingStandScreenHandler);

        this.recipesPage = new BrewingRecipeBookResults();
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
        if (this.selectedTab.getCategory() == BRBRecipeBookCategories.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (this.selectedTab.getCategory() == BRBRecipeBookCategories.BREWING_LINGERING_POTION) {
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

        this.filterButton = new StateSwitchingButton(i + 110, j + 12, 26, 16, this.book.isFilteringCraftable());
        this.updateFilterButtonTooltip();
        this.setBookButtonTexture();

        for (BRBRecipeBookCategories brewingRecipeBookGroup : BRBRecipeBookCategories.getGroups(BRBRecipeBookType.BREWING)) {
            this.tabButtons.add(new BRBGroupButtonWidget(brewingRecipeBookGroup));
        }

        if (this.selectedTab != null) {
            this.selectedTab = this.tabButtons.stream().filter((button) -> button.getCategory().equals(this.selectedTab.getCategory())).findFirst().orElse(null);
        }

        if (this.selectedTab == null) {
            this.selectedTab = this.tabButtons.get(0);
        }

        this.selectedTab.setStateTriggered(true);
        this.updateCollections(false);
        this.refreshTabButtons();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isVisible()) {
            if (this.recipesPageMouseClicked(mouseX, mouseY, button)) {
                BrewableResult result = this.recipesPage.getCurrentClickedRecipe();
                if (result != null) {
                    this.ghostRecipe.clear();

                    if (!result.hasMaterials(this.selectedTab.getCategory(), menu.slots)) {
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

                Iterator<BRBGroupButtonWidget> var6 = this.tabButtons.iterator();

                BRBGroupButtonWidget brewableRecipeGroupButtonWidget;
                do {
                    if (!var6.hasNext()) {
                        return false;
                    }

                    brewableRecipeGroupButtonWidget = var6.next();
                } while (!brewableRecipeGroupButtonWidget.mouseClicked(mouseX, mouseY, button));

                if (this.selectedTab != brewableRecipeGroupButtonWidget) {
                    if (this.selectedTab != null) {
                        this.selectedTab.setStateTriggered(false);
                    }

                    this.selectedTab = brewableRecipeGroupButtonWidget;
                    this.selectedTab.setStateTriggered(true);
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

        assert selectedTab != null;
        ItemStack inputStack = result.inputAsItemStack(selectedTab.getCategory());
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
        if (this.selectedTab == null) return;
        if (this.searchBox == null) return;

        // Create a copy to not mess with the original list
        List<BrewableResult> results = new ArrayList<>(book.getCollectionsForCategory(selectedTab.getCategory()));

        String string = this.searchBox.getValue();
        if (!string.isEmpty()) {
            results.removeIf(itemStack -> !itemStack.ingredient.getHoverName().getString().toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)));
        }

        if (this.book.isFilteringCraftable()) {
            results.removeIf((result) -> !result.hasMaterials(selectedTab.getCategory(), menu.slots));
        }

        this.betterRecipeBook$sortByPinsInPlace(results);

        this.recipesPage.setResults(results, resetCurrentPage, selectedTab.getCategory());
    }

    @Override
    protected boolean selfRecallOpen() {
        return BetterRecipeBook.rememberedBrewingOpen;
    }

    @Override
    protected boolean selfRecallFiltering() {
        return BetterRecipeBook.rememberedBrewingToggle;
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
        for (BRBGroupButtonWidget brewableRecipeGroupButtonWidget : this.tabButtons) {
            brewableRecipeGroupButtonWidget.render(gui, mouseX, mouseY, delta);
        }

        // render the toggle brewable filter button
        this.filterButton.render(gui, mouseX, mouseY, delta);

        this.renderSettingsButton(this.settingsButton, gui, mouseX, mouseY, delta);

        // render the recipe book page contents
        this.recipesPage.render(gui, blitX, blitY, mouseX, mouseY, delta);

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

    @Override
    protected void renderGhostRecipeTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
        ghostRecipe.renderTooltip(gui, x, y, mouseX, mouseY);
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
            for (BrewableAnimatedResultButton resultButton : this.recipesPage.buttons) {
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
        return bl && !bl2 && !this.selectedTab.isHoveredOrFocused();
    }

    @Override
    public void recipesShown(List<RecipeHolder<?>> list) {

    }

    public void recipesUpdated() {
        updateCollections(false);
    }

}
