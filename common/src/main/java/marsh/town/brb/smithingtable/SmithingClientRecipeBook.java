package marsh.town.brb.smithingtable;

import net.minecraft.client.Minecraft;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;

public class SmithingClientRecipeBook extends RecipeBook {
    private boolean filteringCraftable;

    public boolean isFilteringCraftable() {
        return filteringCraftable;
    }

    public List<SmithableResult> getResultsForCategory(SmithingRecipeBookGroup group) {
        List<RecipeHolder<SmithingRecipe>> recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
        List<SmithableResult> results = new ArrayList<>();

        for (RecipeHolder<SmithingRecipe> recipe: recipes) {
            SmithingRecipe value = recipe.value();

            if (group == SmithingRecipeBookGroup.SMITHING_SEARCH) {
                if (value instanceof SmithingTransformRecipe) {
                    results.add(SmithableResult.of((SmithingTransformRecipe) value));
                } else {
                    results.addAll(SmithableResult.of((SmithingTrimRecipe) value));
                }
            } else if (group == SmithingRecipeBookGroup.SMITHING_TRANSFORM) {
                if (value instanceof SmithingTransformRecipe) {
                    results.add(SmithableResult.of((SmithingTransformRecipe) value));
                }
            } else if (value instanceof SmithingTrimRecipe) {
                results.addAll(SmithableResult.of((SmithingTrimRecipe) value));
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
