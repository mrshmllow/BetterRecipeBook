package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class DisableBook {
    @Inject(at = @At("HEAD"), method = "isVisible", cancellable = true)
    public void isOpen(CallbackInfoReturnable<Boolean> cir) {
        if (!BetterRecipeBook.config.enableBook) {
            cir.setReturnValue(false);
        }
    }
}
