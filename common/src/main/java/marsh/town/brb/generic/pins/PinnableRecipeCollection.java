package marsh.town.brb.generic.pins;

import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

public class PinnableRecipeCollection extends RecipeCollection implements Pinnable {
    public PinnableRecipeCollection(RegistryAccess registryAccess, List<Recipe<?>> list) {
        super(registryAccess, list);
    }

    static public PinnableRecipeCollection of(RecipeCollection collection) {
        return new PinnableRecipeCollection(collection.registryAccess(), collection.getRecipes());
    }

    @Override
    public boolean has(ResourceLocation resourceLocation) {
        for (Recipe<?> recipe : getRecipes()) {
            if (recipe.getId().equals(resourceLocation)) {
                return true;
            }
        }
        return false;
    }
}
