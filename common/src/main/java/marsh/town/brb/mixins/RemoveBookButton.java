package marsh.town.brb.mixins;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ImageButton.class)
public class RemoveBookButton extends Button {
    @Final
    @Shadow
    protected ResourceLocation resourceLocation;

    @Unique
    ResourceLocation betterRecipeBook$RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");

    public RemoveBookButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Inject(at = @At("HEAD"), method = "renderWidget", cancellable = true)
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!BetterRecipeBook.config.enableBook && resourceLocation == betterRecipeBook$RECIPE_BUTTON_LOCATION) {
            this.visible = false;
            ci.cancel();
        }
    }
}
