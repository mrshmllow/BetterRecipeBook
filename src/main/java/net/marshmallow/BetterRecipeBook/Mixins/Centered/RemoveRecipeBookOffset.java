package net.marshmallow.BetterRecipeBook.Mixins.Centered;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RecipeBookWidget.class)
public class RemoveRecipeBookOffset extends DrawableHelper {
    @Shadow private int leftOffset;
    @Shadow private boolean narrow;

    @Inject(
            method = "reset",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;leftOffset:I")
    )
    public void center(CallbackInfo ci) {
        if (BetterRecipeBook.config.keepCentered) {
            this.leftOffset = this.narrow ? 0 : 162;
        } else {
            this.leftOffset = this.narrow ? 0 : 86;
        }
    }

    @Inject(method = "findLeftEdge", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "RETURN"), cancellable = true)
    public void findLeftEdge(int width, int backgroundWidth, CallbackInfoReturnable<Integer> cir, int j) {
        if (BetterRecipeBook.config.keepCentered) {
            j = (width - backgroundWidth) / 2;
        }
        cir.setReturnValue(j);
    }
}
