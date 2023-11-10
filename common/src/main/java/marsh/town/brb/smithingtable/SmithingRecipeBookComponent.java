package marsh.town.brb.smithingtable;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.enums.BRBRecipeBookType;
import marsh.town.brb.generic.GenericRecipeBookComponent;
import marsh.town.brb.interfaces.IPinningComponent;
import marsh.town.brb.recipe.BRBSmithingRecipe;
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
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class SmithingRecipeBookComponent extends GenericRecipeBookComponent<SmithingMenu, SmithingRecipeBookPage, SmithingClientRecipeBook> implements IPinningComponent<SmithingRecipeCollection> {
    private static final MutableComponent ONLY_CRAFTABLES_TOOLTIP = Component.translatable("brb.gui.smithable");
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

    @Override
    public void handlePlaceRecipe() {
        BRBSmithingRecipe result = this.recipesPage.getCurrentClickedRecipe();
        SmithingRecipeCollection recipeCollection = this.recipesPage.getLastClickedRecipeCollection();

        if (result == null || recipeCollection == null) return;

        this.ghostRecipe.clear();

        if (!result.hasMaterials(this.menu.slots, registryAccess)) {
            this.setupGhostRecipe(result, this.menu.slots);
            return;
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

    public boolean toggleFiltering() {
        boolean bl = !this.book.isFilteringCraftable();
        this.book.setFilteringCraftable(bl);
        BetterRecipeBook.rememberedSmithableToggle = bl;
        return bl;
    }

    @Override
    public void recipesShown(List<RecipeHolder<?>> list) {

    }
}
