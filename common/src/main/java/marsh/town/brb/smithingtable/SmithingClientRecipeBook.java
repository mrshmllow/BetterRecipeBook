package marsh.town.brb.smithingtable;

import marsh.town.brb.smithingtable.recipe.BRBSmithingTransformRecipe;
import marsh.town.brb.smithingtable.recipe.BRBSmithingTrimRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;

public class SmithingClientRecipeBook extends RecipeBook {
    private boolean filteringCraftable;

    public boolean isFilteringCraftable() {
        return filteringCraftable;
    }

    public List<SmithingRecipeCollection> getCollectionsForCategory(SmithingRecipeBookGroup group, SmithingMenu smithingScreenHandler, RegistryAccess registryAccess, RecipeManager recipeManager) {
        List<RecipeHolder<SmithingRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeType.SMITHING);
        List<SmithingRecipeCollection> results = new ArrayList<>();

        for (RecipeHolder<SmithingRecipe> recipe : recipes) {
            SmithingRecipe value = recipe.value();

            if (group == SmithingRecipeBookGroup.SMITHING_SEARCH) {
                if (value instanceof SmithingTransformRecipe) {
                    results.add(new SmithingRecipeCollection(List.of(BRBSmithingTransformRecipe.from((SmithingTransformRecipe) value, registryAccess)), smithingScreenHandler, registryAccess));
                } else if (value instanceof SmithingTrimRecipe) {
                    results.add(new SmithingRecipeCollection(BRBSmithingTrimRecipe.from((SmithingTrimRecipe) value), smithingScreenHandler, registryAccess));
                }
            } else if (group == SmithingRecipeBookGroup.SMITHING_TRANSFORM) {
                if (value instanceof SmithingTransformRecipe) {
                    results.add(new SmithingRecipeCollection(List.of(BRBSmithingTransformRecipe.from((SmithingTransformRecipe) value, registryAccess)), smithingScreenHandler, registryAccess));
                }
            } else if (value instanceof SmithingTrimRecipe) {
                results.add(new SmithingRecipeCollection(BRBSmithingTrimRecipe.from((SmithingTrimRecipe) value), smithingScreenHandler, registryAccess));
            }
        }

        return results;
    }

    public boolean isFiltering(RecipeBookType category) {
        return filteringCraftable;
    }

    public void setFilteringCraftable(boolean filteringCraftable) {
        this.filteringCraftable = filteringCraftable;
    }
}
