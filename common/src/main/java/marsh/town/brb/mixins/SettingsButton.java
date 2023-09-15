package marsh.town.brb.mixins;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.config.Config;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class SettingsButton {
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("brb:textures/gui/buttons.png");
    @Shadow
    protected Minecraft minecraft;

    @Shadow private int height;
    @Shadow private int width;
    @Shadow private int xOffset;

    @Shadow public abstract boolean isVisible();

    protected ImageButton settingsButton;
    private static final Component OPEN_SETTINGS_TEXT;

    @Inject(method = "initVisuals", at = @At("RETURN"))
    public void reset(CallbackInfo ci) {
        if (BetterRecipeBook.config.settingsButton) {
            int i = (this.width - 147) / 2 - this.xOffset;
            int j = (this.height - 166) / 2;

            int u = 0;
            if (BetterRecipeBook.config.darkMode) {
                u = 18;
            }

            this.settingsButton = new ImageButton(i + 11, j + 137, 16, 18, u, 77, 19, BUTTON_TEXTURE, button -> {
                Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, Minecraft.getInstance().screen).get());
            });
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.settingsButton != null && BetterRecipeBook.config.settingsButton && this.isVisible()) {
            if (this.settingsButton.mouseClicked(mouseX, mouseY, button)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;render(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V"))
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (BetterRecipeBook.config.settingsButton) {
            this.settingsButton.render(gui, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;renderGhostRecipeTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"))
    public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.settingsButton == null) return;
        if (this.settingsButton.isHoveredOrFocused() && BetterRecipeBook.config.settingsButton) {
            if (this.minecraft.screen != null) {
                gui.renderComponentTooltip(minecraft.font, List.of(OPEN_SETTINGS_TEXT), mouseX, mouseY);
            }
        }
    }

    static {
        OPEN_SETTINGS_TEXT = Component.translatable("brb.gui.settings.open");
    }
}
