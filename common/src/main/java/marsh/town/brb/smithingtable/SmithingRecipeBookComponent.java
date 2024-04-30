package marsh.town.brb.smithingtable;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.api.BRBBookSettings;
import marsh.town.brb.generic.GenericRecipeBookComponent;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import marsh.town.brb.recipe.smithing.BRBSmithingTransformRecipe;
import marsh.town.brb.recipe.smithing.BRBSmithingTrimRecipe;
import marsh.town.brb.util.BRBHelper;
import marsh.town.brb.util.ClientInventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SmithingRecipeBookComponent extends GenericRecipeBookComponent<SmithingMenu, SmithingRecipeCollection, BRBSmithingRecipe> {
    private static final MutableComponent ONLY_CRAFTABLES_TOOLTIP = Component.translatable("brb.gui.smithable");

    public void init(int width, int height, Minecraft minecraft, boolean widthNarrow, SmithingMenu menu, Consumer<ItemStack> onGhostRecipeUpdate, RegistryAccess registryAccess, RecipeManager recipeManager) {
        super.init(width, height, minecraft, widthNarrow, menu, onGhostRecipeUpdate, registryAccess);

        this.recipeManager = recipeManager;
        this.ghostRecipe = new SmithingGhostRecipe(onGhostRecipeUpdate, registryAccess);
        this.ghostRecipe.setDefaultRenderingPredicate(this.menu);
        this.recipesPage = new SmithingRecipeBookPage(registryAccess, () -> BRBBookSettings.isFiltering(getRecipeBookType()));

//        if (this.isVisible()) {
        this.initVisuals();
//        }
    }

    @Override
    public Component getRecipeFilterName() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    @Override
    public BRBHelper.Book getRecipeBookType() {
        return BetterRecipeBook.SMITHING;
    }

    @Override
    public void handlePlaceRecipe() {
        BRBSmithingRecipe result = this.recipesPage.getCurrentClickedRecipe();
        SmithingRecipeCollection recipeCollection = this.recipesPage.getLastClickedRecipeCollection();

        if (result == null || recipeCollection == null) return;

        this.ghostRecipe.clear();

        if (!result.hasMaterials(this.menu.slots, this.registryAccess)) {
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
            } else if (!placedBase && /*TODO test*/itemStack.get(DataComponents.TRIM) == null && result.getBase().getItem().equals(itemStack.getItem())) {
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

        this.ghostRecipe.addIngredient(SmithingMenu.ADDITIONAL_SLOT, result.getAddition(), SmithingMenu.ADDITIONAL_SLOT_X_PLACEMENT, SmithingMenu.SLOT_Y_PLACEMENT);
        this.ghostRecipe.addIngredient(SmithingMenu.TEMPLATE_SLOT, result.getTemplate(), SmithingMenu.TEMPLATE_SLOT_X_PLACEMENT, SmithingMenu.SLOT_Y_PLACEMENT);
        this.ghostRecipe.addIngredient(SmithingMenu.BASE_SLOT, Ingredient.of(result.getBase()), SmithingMenu.BASE_SLOT_X_PLACEMENT, SmithingMenu.SLOT_Y_PLACEMENT);
    }

    public boolean isShowingGhostRecipe() {
        return this.ghostRecipe != null && this.ghostRecipe.size() > 0;
    }

    @Override
    protected List<SmithingRecipeCollection> getCollectionsForCategory() {
        List<RecipeHolder<SmithingRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeType.SMITHING);
        List<SmithingRecipeCollection> results = new ArrayList<>();
        BRBBookCategories.Category category = selectedTab.getCategory();

        for (RecipeHolder<SmithingRecipe> recipe : recipes) {
            SmithingRecipe value = recipe.value();

            if (category == BetterRecipeBook.SMITHING_SEARCH) {
                if (value instanceof SmithingTransformRecipe) {
                    results.add(new SmithingRecipeCollection(List.of(BRBSmithingTransformRecipe.from((SmithingTransformRecipe) value, registryAccess)), this.menu, registryAccess));
                } else if (value instanceof SmithingTrimRecipe) {
                    results.add(new SmithingRecipeCollection(BRBSmithingTrimRecipe.from((SmithingTrimRecipe) value), this.menu, registryAccess));
                }
            } else if (category == BetterRecipeBook.SMITHING_TRANSFORM) {
                if (value instanceof SmithingTransformRecipe) {
                    results.add(new SmithingRecipeCollection(List.of(BRBSmithingTransformRecipe.from((SmithingTransformRecipe) value, registryAccess)), this.menu, registryAccess));
                }
            } else if (value instanceof SmithingTrimRecipe) {
                results.add(new SmithingRecipeCollection(BRBSmithingTrimRecipe.from((SmithingTrimRecipe) value), this.menu, registryAccess));
            }
        }

        return results;
    }
}
