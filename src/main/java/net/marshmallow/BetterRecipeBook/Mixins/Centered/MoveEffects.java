package net.marshmallow.BetterRecipeBook.Mixins.Centered;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractInventoryScreen.class)
public class MoveEffects {
    /* Credit to https://github.com/Reavershark on GitHub. https://github.com/Reavershark/centered-inventory */

    @Shadow
    protected boolean drawStatusEffects;

    @Inject(method = "applyStatusEffectOffset()V", at = @At(value = "HEAD"), cancellable = true)
    protected void noStatusEffectOffset(CallbackInfo cir) {
        if (BetterRecipeBook.config.statusEffects) {
            cir.cancel();
        }
    }

    @ModifyVariable(method = "drawStatusEffects(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At("STORE"), ordinal = 0)
    private int moveEffectsRight(int i) {
        if (BetterRecipeBook.config.statusEffects) {
            assert MinecraftClient.getInstance().player != null;
            if (MinecraftClient.getInstance().player.isCreative()) {
                return i + 323;
            } else {
                return i + 304;
            }
        } else {
            return i;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At(value = "HEAD"))
    public void allwaysRenderEffects(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (BetterRecipeBook.config.statusEffects) {
            this.drawStatusEffects = true;
        }
    }
}
