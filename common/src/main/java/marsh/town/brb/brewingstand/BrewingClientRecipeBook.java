package marsh.town.brb.brewingstand;

import marsh.town.brb.generic.GenericClientRecipeBook;
import marsh.town.brb.loaders.PotionLoader;
import marsh.town.brb.recipe.BRBRecipeBookCategory;

import java.util.List;

public class BrewingClientRecipeBook extends GenericClientRecipeBook {
    public List<BrewableResult> getCollectionsForCategory(BRBRecipeBookCategory category) {
        List<BrewableResult> results = PotionLoader.POTIONS;

        if (category == BRBRecipeBookCategory.BREWING_SPLASH_POTION) {
            results = PotionLoader.SPLASHES;
        } else if (category == BRBRecipeBookCategory.BREWING_LINGERING_POTION) {
            results = PotionLoader.LINGERINGS;
        }

        return results;
    }
}
