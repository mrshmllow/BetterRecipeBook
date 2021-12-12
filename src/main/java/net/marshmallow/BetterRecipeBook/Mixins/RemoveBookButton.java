package net.marshmallow.BetterRecipeBook.Mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ImageButton.class)
public class RemoveBookButton extends Button {
    @Final
    @Shadow
    private ResourceLocation resourceLocation;

    public RemoveBookButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress);
    }

    @Inject(at = @At("HEAD"), method = "renderButton", cancellable = true)
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (resourceLocation.equals(new ResourceLocation("minecraft:textures/gui/recipe_button.png")) && !BetterRecipeBook.config.enableBook) {
            this.visible = false;
            ci.cancel();
        }
    }
}
