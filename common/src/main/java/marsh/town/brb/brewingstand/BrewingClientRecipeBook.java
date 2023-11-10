package marsh.town.brb.brewingstand;

import marsh.town.brb.generic.GenericClientRecipeBook;
import marsh.town.brb.loaders.PotionLoader;
import marsh.town.brb.recipe.BRBRecipeBookCategories;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookType;

import java.util.List;

public class BrewingClientRecipeBook extends RecipeBook implements GenericClientRecipeBook {
    private boolean filteringCraftable;

    public boolean isFilteringCraftable() {
        return filteringCraftable;
    }

    public List<BrewableResult> getResultsForCategory(BRBRecipeBookCategories category) {
        List<BrewableResult> results = PotionLoader.POTIONS;

        if (category == BRBRecipeBookCategories.BREWING_SPLASH_POTION) {
            results = PotionLoader.SPLASHES;
        } else if (category == BRBRecipeBookCategories.BREWING_LINGERING_POTION) {
            results = PotionLoader.LINGERINGS;
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
