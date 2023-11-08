package marsh.town.brb.interfaces.unlockrecipes;

import net.minecraft.resources.ResourceLocation;

import java.util.Set;

/**
 * Access interface for RecipeManagerMixin
 */
public interface IMixinRecipeManager {

    Set<ResourceLocation> betterRecipeBook$getServerUnlockedRecipes();

}
