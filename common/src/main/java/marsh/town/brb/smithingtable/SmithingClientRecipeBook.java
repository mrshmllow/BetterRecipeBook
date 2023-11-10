package marsh.town.brb.smithingtable;

import marsh.town.brb.generic.GenericClientRecipeBook;
import marsh.town.brb.recipe.BRBRecipeBookCategory;
import marsh.town.brb.recipe.smithing.BRBSmithingTransformRecipe;
import marsh.town.brb.recipe.smithing.BRBSmithingTrimRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;

public class SmithingClientRecipeBook extends GenericClientRecipeBook {
    public List<SmithingRecipeCollection> getCollectionsForCategory(BRBRecipeBookCategory category, SmithingMenu smithingScreenHandler, RegistryAccess registryAccess, RecipeManager recipeManager) {
        List<RecipeHolder<SmithingRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeType.SMITHING);
        List<SmithingRecipeCollection> results = new ArrayList<>();

        for (RecipeHolder<SmithingRecipe> recipe : recipes) {
            SmithingRecipe value = recipe.value();

            if (category == BRBRecipeBookCategory.SEARCH) {
                if (value instanceof SmithingTransformRecipe) {
                    results.add(new SmithingRecipeCollection(List.of(BRBSmithingTransformRecipe.from((SmithingTransformRecipe) value, registryAccess)), smithingScreenHandler, registryAccess));
                } else if (value instanceof SmithingTrimRecipe) {
                    results.add(new SmithingRecipeCollection(BRBSmithingTrimRecipe.from((SmithingTrimRecipe) value), smithingScreenHandler, registryAccess));
                }
            } else if (category == BRBRecipeBookCategory.SMITHING_TRANSFORM) {
                if (value instanceof SmithingTransformRecipe) {
                    results.add(new SmithingRecipeCollection(List.of(BRBSmithingTransformRecipe.from((SmithingTransformRecipe) value, registryAccess)), smithingScreenHandler, registryAccess));
                }
            } else if (value instanceof SmithingTrimRecipe) {
                results.add(new SmithingRecipeCollection(BRBSmithingTrimRecipe.from((SmithingTrimRecipe) value), smithingScreenHandler, registryAccess));
            }
        }

        return results;
    }
}
