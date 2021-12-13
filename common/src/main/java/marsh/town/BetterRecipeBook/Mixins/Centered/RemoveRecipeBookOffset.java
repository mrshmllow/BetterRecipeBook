package marsh.town.BetterRecipeBook.Mixins.Centered;

import marsh.town.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RecipeBookComponent.class)
public class RemoveRecipeBookOffset extends GuiComponent {
    @Shadow private int xOffset;
    @Shadow private boolean widthTooNarrow;

    @Inject(
            method = "initVisuals",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;xOffset:I")
    )
    public void center(CallbackInfo ci) {
        if (BetterRecipeBook.config.keepCentered) {
            this.xOffset = this.widthTooNarrow ? 0 : 162;
        } else {
            this.xOffset = this.widthTooNarrow ? 0 : 86;
        }
    }

    @Inject(method = "updateScreenPosition", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "RETURN"), cancellable = true)
    public void findLeftEdge(int width, int backgroundWidth, CallbackInfoReturnable<Integer> cir, int j) {
        if (BetterRecipeBook.config.keepCentered) {
            j = (width - backgroundWidth) / 2;
        }
        cir.setReturnValue(j);
    }

    @Inject(method = "isOffsetNextToMainGUI", at = @At("RETURN"), cancellable = true)
    public void isWide(CallbackInfoReturnable<Boolean> cir) {
        if (this.xOffset == 162 || this.xOffset == 86) {
            cir.setReturnValue(true);
        }
    }
}
