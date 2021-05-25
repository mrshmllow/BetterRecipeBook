package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {
    @Inject(at = @At("HEAD"), method = "isOpen", cancellable = true)
    public void isOpen(CallbackInfoReturnable<Boolean> cir) {
        if (!BetterRecipeBook.config.enableBook) {
            cir.setReturnValue(false);
        }
    }
}
