package marsh.town.brb.BrewingStand;

import marsh.town.brb.Loaders.PotionLoader;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookType;

import java.util.List;

public class ClientRecipeBook extends RecipeBook {
    private boolean filteringCraftable;

    public boolean isFilteringCraftable() {
        return filteringCraftable;
    }

    public List<Result> getResultsForCategory(RecipeBookGroup group) {
        List<Result> results = PotionLoader.POTIONS;

        if (group == RecipeBookGroup.BREWING_SPLASH_POTION) {
            results = PotionLoader.SPLASHES;
        } else if (group == RecipeBookGroup.BREWING_LINGERING_POTION) {
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
