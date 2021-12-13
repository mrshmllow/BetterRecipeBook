package marsh.town.BetterRecipeBook.Mixins;

import marsh.town.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseScrollHandler {
    @Shadow
    private double accumulatedScroll;

    @Final @Shadow
    private Minecraft minecraft;

    @Inject(at = @At(value = "RETURN"), method = "onScroll")
    public void onMouseScroll(long window, double arg1, double vertical, CallbackInfo ci) {
        if (BetterRecipeBook.queuedScroll == 0 && BetterRecipeBook.config.scrolling.enableScrolling) {
            assert minecraft.player != null;

            double d = (this.minecraft.options.discreteMouseScroll ? Math.signum(vertical) : vertical) * this.minecraft.options.mouseWheelSensitivity;

            BetterRecipeBook.queuedScroll = (int) -((int) this.accumulatedScroll + d);
        }
    }
}
