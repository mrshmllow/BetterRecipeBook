package net.marshmallow.BetterRecipeBook.Mixins;

import me.shedaniel.autoconfig.AutoConfig;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.marshmallow.BetterRecipeBook.Config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public abstract class SettingsButton {
    private static final Identifier BUTTON_TEXTURE = new Identifier("betterrecipebook:textures/gui/buttons.png");
    @Shadow
    protected MinecraftClient client;

    @Shadow private int parentHeight;
    @Shadow private int parentWidth;
    @Shadow private int leftOffset;

    @Shadow public abstract boolean isOpen();

    protected TexturedButtonWidget settingsButton;
    private static final Text OPEN_SETTINGS_TEXT;

    @Inject(method = "reset", at = @At("RETURN"))
    public void reset(CallbackInfo ci) {
        if (BetterRecipeBook.config.settingsButton) {
            int i = (this.parentWidth - 147) / 2 - this.leftOffset;
            int j = (this.parentHeight - 166) / 2;

            int u = 0;
            if (BetterRecipeBook.config.darkMode) {
                u = 18;
            }

            this.settingsButton = new TexturedButtonWidget(i + 11, j + 137, 16, 16, u, 73, 18, BUTTON_TEXTURE, button -> {
                MinecraftClient.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, MinecraftClient.getInstance().currentScreen).get());
            });
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.settingsButton != null) {
            if (this.settingsButton.mouseClicked(mouseX, mouseY, button) && this.isOpen() && BetterRecipeBook.config.settingsButton) {
                assert this.client.player != null;
                if (!this.client.player.isSpectator()) {
                    if (!this.client.player.isSpectator()) {
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookResults;draw(Lnet/minecraft/client/util/math/MatrixStack;IIIIF)V"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (BetterRecipeBook.config.settingsButton) {
            this.settingsButton.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "drawTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;drawGhostSlotTooltip(Lnet/minecraft/client/util/math/MatrixStack;IIII)V"))
    public void drawTooltip(MatrixStack matrices, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.settingsButton.isHovered() && BetterRecipeBook.config.settingsButton) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.renderTooltip(matrices, OPEN_SETTINGS_TEXT, mouseX, mouseY);
            }
        }
    }

    static {
        OPEN_SETTINGS_TEXT = new TranslatableText("betterrecipebook.gui.settings.open");
    }
}
