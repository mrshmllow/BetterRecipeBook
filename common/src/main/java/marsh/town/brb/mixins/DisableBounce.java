package marsh.town.brb.mixins;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBook.class)
public class DisableBounce {
    @Inject(method = "willHighlight", at = @At(value = "HEAD"), cancellable = true)
    public void willHighlight(RecipeHolder<?> recipeHolder, CallbackInfoReturnable<Boolean> cir) {
        if (!BetterRecipeBook.config.newRecipes.enableBounce) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
