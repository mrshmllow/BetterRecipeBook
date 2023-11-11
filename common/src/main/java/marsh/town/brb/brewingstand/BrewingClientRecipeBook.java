package marsh.town.brb.brewingstand;

import marsh.town.brb.generic.GenericClientRecipeBook;
import marsh.town.brb.loaders.PotionLoader;
import marsh.town.brb.recipe.BRBRecipeBookCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.BrewingStandMenu;

import java.util.ArrayList;
import java.util.List;

public class BrewingClientRecipeBook extends GenericClientRecipeBook {
    public List<BrewingRecipeCollection> getCollectionsForCategory(BRBRecipeBookCategory category, BrewingStandMenu menu, RegistryAccess registryAccess) {
        List<BrewingRecipeCollection> results = new ArrayList<>();

        if (category == BRBRecipeBookCategory.BREWING_POTION) {
            for (BrewableResult potion : PotionLoader.POTIONS) {
                results.add(new BrewingRecipeCollection(List.of(potion), menu, registryAccess, category));
            }
        } else if (category == BRBRecipeBookCategory.BREWING_SPLASH_POTION) {
            for (BrewableResult splash : PotionLoader.SPLASHES) {
                results.add(new BrewingRecipeCollection(List.of(splash), menu, registryAccess, category));
            }
        } else if (category == BRBRecipeBookCategory.BREWING_LINGERING_POTION) {
            for (BrewableResult splash : PotionLoader.LINGERINGS) {
                results.add(new BrewingRecipeCollection(List.of(splash), menu, registryAccess, category));
            }
        }

        return results;
    }
}
