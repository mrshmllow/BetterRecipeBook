package net.marshmallow.BetterRecipeBook.Mixins.InstantCraft;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "clickRecipe", at = @At("TAIL"))
    private void clickRecipe(int syncId, Recipe<?> recipe, boolean craftAll, CallbackInfo ci) {
        BetterRecipeBook.instantCraftingManager.recipeClicked(recipe);
    }
}