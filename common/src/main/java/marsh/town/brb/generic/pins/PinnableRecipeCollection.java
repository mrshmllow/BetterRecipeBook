package marsh.town.brb.generic.pins;

import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class PinnableRecipeCollection extends RecipeCollection implements Pinnable {
    public PinnableRecipeCollection(RegistryAccess registryAccess, List<RecipeHolder<?>> list) {
        super(registryAccess, list);
    }

    static public PinnableRecipeCollection of(RecipeCollection collection) {
        return new PinnableRecipeCollection(collection.registryAccess(), collection.getRecipes());
    }

    @Override
    public boolean has(ResourceLocation resourceLocation) {
        for (RecipeHolder<?> recipe : getRecipes()) {
            if (recipe.id().equals(resourceLocation)) {
                return true;
            }
        }
        return false;
    }
}
