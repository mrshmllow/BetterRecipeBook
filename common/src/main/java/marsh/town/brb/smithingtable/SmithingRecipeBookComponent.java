package marsh.town.brb.smithingtable;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.enums.BRBRecipeBookType;
import marsh.town.brb.generic.BRBGroupButtonWidget;
import marsh.town.brb.generic.GenericRecipeBookComponent;
import marsh.town.brb.interfaces.IPinningComponent;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import marsh.town.brb.util.BRBTextures;
import marsh.town.brb.util.ClientInventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class SmithingRecipeBookComponent extends GenericRecipeBookComponent<SmithingMenu, SmithingRecipeBookPage, SmithingClientRecipeBook> implements IPinningComponent<SmithingRecipeCollection> {
    private static final MutableComponent ONLY_CRAFTABLES_TOOLTIP = Component.translatable("brb.gui.smithable");
    boolean doubleRefresh = true;
    @Nullable
    public SmithingGhostRecipe ghostRecipe;
    private RegistryAccess registryAccess;
    private RecipeManager recipeManager;

    public SmithingRecipeBookComponent() {
        super(SmithingClientRecipeBook::new);
    }

    public void init(int width, int height, Minecraft minecraft, boolean widthNarrow, SmithingMenu menu, Consumer<SmithingGhostRecipe> onGhostRecipeUpdate, RegistryAccess registryAccess, RecipeManager recipeManager) {
        super.init(width, height, minecraft, widthNarrow, menu);

        this.registryAccess = registryAccess;
        this.recipeManager = recipeManager;
        this.ghostRecipe = new SmithingGhostRecipe(onGhostRecipeUpdate, registryAccess);
        this.recipesPage = new SmithingRecipeBookPage(registryAccess);

//        if (this.isVisible()) {
        this.initVisuals();
//        }
    }

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
        for (BRBGroupButtonWidget smithingRecipeGroupButtonWidget : this.tabButtons) {
            smithingRecipeGroupButtonWidget.render(gui, mouseX, mouseY, delta);
        }

        this.filterButton.render(gui, mouseX, mouseY, delta);

        this.renderSettingsButton(gui, mouseX, mouseY, delta);

        // render the recipe book page contents
        this.recipesPage.render(gui, blitX, blitY, mouseX, mouseY, delta);

        gui.pose().popPose();
    }

    @Override
    public void updateCollections(boolean resetCurrentPage) {
        if (this.selectedTab == null) return;
        if (this.searchBox == null) return;

        // Create a copy to not mess with the original list
        List<SmithingRecipeCollection> results = new ArrayList<>(book.getCollectionsForCategory(selectedTab.getCategory(), (SmithingMenu) menu, registryAccess, recipeManager));

        String string = this.searchBox.getValue();
        if (!string.isEmpty()) {
            results.removeIf(collection -> !collection.getFirst().getTemplateType().toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)));
        }

        if (this.book.isFilteringCraftable()) {
            results.removeIf((result) -> !result.atleastOneCraftable(this.menu.slots));
        }

        this.betterRecipeBook$sortByPinsInPlace(results);

        this.recipesPage.setResults(results, resetCurrentPage, selectedTab.getCategory());
    }

    @Override
    protected boolean selfRecallOpen() {
        return BetterRecipeBook.rememberedSmithingOpen;
    }

    @Override
    protected boolean selfRecallFiltering() {
        return BetterRecipeBook.rememberedSmithableToggle;
    }

    @Override
    public Component getRecipeFilterName() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    @Override
    public BRBRecipeBookType getRecipeBookType() {
        return BRBRecipeBookType.SMITHING;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_F && BetterRecipeBook.config.enablePinning) {
            for (SmithableRecipeButton resultButton : this.recipesPage.buttons) {
                if (resultButton.isHoveredOrFocused()) {
                    BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavouriteSmithing(resultButton.getCollection().getFirst());
                    this.updateCollections(false);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isVisible()) {
            if (this.recipesPageMouseClicked(mouseX, mouseY, button)) {
                BRBSmithingRecipe result = this.recipesPage.getCurrentClickedRecipe();
                SmithingRecipeCollection recipeCollection = this.recipesPage.getLastClickedRecipeCollection();

                if (result != null && recipeCollection != null) {
                    this.ghostRecipe.clear();

                    if (!result.hasMaterials(this.menu.slots, registryAccess)) {
                        this.setupGhostRecipe(result, this.menu.slots);
                        return true;
                    }

                    int slotIndex = 0;
                    boolean placedBase = false;
                    for (Slot slot : menu.slots) {
                        ItemStack itemStack = slot.getItem();

                        if (result.getTemplate().test(itemStack)) {
                            assert Minecraft.getInstance().gameMode != null;
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, SmithingMenu.TEMPLATE_SLOT, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                        } else if (!placedBase && ArmorTrim.getTrim(registryAccess, itemStack, true).isEmpty() && result.getBase().getItem().equals(itemStack.getItem())) {
                            assert Minecraft.getInstance().gameMode != null;
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, SmithingMenu.BASE_SLOT, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            placedBase = true;
                        } else if (result.getAddition().test(itemStack)) {
                            assert Minecraft.getInstance().gameMode != null;
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, SmithingMenu.ADDITIONAL_SLOT, 0, ClickType.PICKUP, Minecraft.getInstance().player);
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
//                    this.sendUpdateSettings();
                    BetterRecipeBook.rememberedBrewingToggle = bl;
                    this.updateCollections(false);
                    return true;
                } else if (this.settingsButtonMouseClicked(mouseX, mouseY, button)) {
                    return true;
                }

                Iterator<BRBGroupButtonWidget> tabButtonsIter = this.tabButtons.iterator();

                BRBGroupButtonWidget smithingRecipeGroupButtonWidget;
                do {
                    if (!tabButtonsIter.hasNext()) {
                        return false;
                    }

                    smithingRecipeGroupButtonWidget = tabButtonsIter.next();
                } while (!smithingRecipeGroupButtonWidget.mouseClicked(mouseX, mouseY, button));

                if (this.selectedTab != smithingRecipeGroupButtonWidget) {
                    if (this.selectedTab != null) {
                        this.selectedTab.setStateTriggered(false);
                    }

                    this.selectedTab = smithingRecipeGroupButtonWidget;
                    this.selectedTab.setStateTriggered(true);
                    this.updateCollections(true);
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public void setupGhostRecipe(BRBSmithingRecipe result, List<Slot> list) {
        this.ghostRecipe.setRecipe(result);

        this.ghostRecipe.addIngredient(result.getAddition(), SmithingMenu.ADDITIONAL_SLOT_X_PLACEMENT, SmithingMenu.SLOT_Y_PLACEMENT);
        this.ghostRecipe.addIngredient(result.getTemplate(), SmithingMenu.TEMPLATE_SLOT_X_PLACEMENT, SmithingMenu.SLOT_Y_PLACEMENT);
        this.ghostRecipe.addIngredient(Ingredient.of(result.getBase()), SmithingMenu.BASE_SLOT_X_PLACEMENT, SmithingMenu.SLOT_Y_PLACEMENT);
    }

    public void renderGhostRecipe(GuiGraphics guiGraphics, int i, int j, boolean bl, float f) {
        this.ghostRecipe.render(guiGraphics, this.minecraft, i, j, bl, f);
    }

    public void renderGhostRecipeTooltip(GuiGraphics guiGraphics, int i, int j, int k, int l) {
        ItemStack itemStack = null;

        for (int m = 0; m < this.ghostRecipe.size(); ++m) {
            SmithingGhostRecipe.SmithingGhostIngredient ghostIngredient = this.ghostRecipe.get(m);
            int n = ghostIngredient.getX() + i;
            int o = ghostIngredient.getY() + j;
            if (k >= n && l >= o && k < n + 16 && l < o + 16) {
                itemStack = ghostIngredient.getItem();
            }
        }

        if (itemStack != null && this.minecraft.screen != null) {
            guiGraphics.renderComponentTooltip(this.minecraft.font, Screen.getTooltipFromItem(this.minecraft, itemStack), k, l);
        }
    }

    public boolean isShowingGhostRecipe() {
        return this.ghostRecipe != null && this.ghostRecipe.size() > 0;
    }

    private boolean toggleFiltering() {
        boolean bl = !this.book.isFilteringCraftable();
        this.book.setFilteringCraftable(bl);
        BetterRecipeBook.rememberedSmithableToggle = bl;
        return bl;
    }

    @Override
    public void recipesShown(List<RecipeHolder<?>> list) {

    }
}
