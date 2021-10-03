package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TexturedButtonWidget.class)
public class RemoveBookButton extends ButtonWidget {
    @Final
    @Shadow
    private Identifier texture;

    public RemoveBookButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress);
    }

    @Inject(at = @At("HEAD"), method = "renderButton(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", cancellable = true)
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (texture.equals(new Identifier("minecraft:textures/gui/recipe_button.png")) && !BetterRecipeBook.config.enableBook) {
            this.visible = false;
            ci.cancel();
        }
    }
}
