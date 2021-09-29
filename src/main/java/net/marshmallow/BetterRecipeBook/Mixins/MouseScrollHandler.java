package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseScrollHandler {
    @Shadow
    private double eventDeltaWheel;

    @Final @Shadow
    private MinecraftClient client;

    @Inject(at = @At(value = "RETURN"), method = "onMouseScroll")
    public void onMouseScroll(long window, double arg1, double vertical, CallbackInfo ci) {
        if (BetterRecipeBook.queuedScroll == 0 && BetterRecipeBook.config.scrollingModule.enableScrolling) {
            assert client.player != null;

            double d = (this.client.options.discreteMouseScroll ? Math.signum(vertical) : vertical) * this.client.options.mouseWheelSensitivity;

            BetterRecipeBook.queuedScroll = (int) -((int) this.eventDeltaWheel + d);
        }
    }
}
