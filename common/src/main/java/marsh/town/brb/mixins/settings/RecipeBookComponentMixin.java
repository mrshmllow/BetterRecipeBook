package marsh.town.brb.mixins.settings;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.config.Config;
import marsh.town.brb.util.BRBTextures;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookComponentMixin {

    @Shadow
    protected Minecraft minecraft;

    @Shadow private int height;
    @Shadow private int width;
    @Shadow private int xOffset;

    @Shadow public abstract boolean isVisible();

    @Unique protected ImageButton _$settingsButton;
    @Unique private static final Component OPEN_SETTINGS_TEXT;

    @Inject(method = "initVisuals", at = @At("RETURN"))
    public void reset(CallbackInfo ci) {
        if (BetterRecipeBook.config.settingsButton) {
            int i = (this.width - 147) / 2 - this.xOffset;
            int j = (this.height - 166) / 2;

            this._$settingsButton = new ImageButton(i + 11, j + 137, 16, 16, BRBTextures.SETTINGS_BUTTON_SPRITES, button -> {
                Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, Minecraft.getInstance().screen).get());
            });
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this._$settingsButton != null && BetterRecipeBook.config.settingsButton && this.isVisible()) {
            if (this._$settingsButton.mouseClicked(mouseX, mouseY, button)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;render(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V"))
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (BetterRecipeBook.config.settingsButton) {
            this._$settingsButton.render(gui, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;renderGhostRecipeTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"))
    public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
        if (this._$settingsButton == null) return;
        if (this._$settingsButton.isHoveredOrFocused() && BetterRecipeBook.config.settingsButton) {
            if (this.minecraft.screen != null) {
                gui.renderComponentTooltip(minecraft.font, List.of(OPEN_SETTINGS_TEXT), mouseX, mouseY);
            }
        }
    }

    static {
        OPEN_SETTINGS_TEXT = Component.translatable("brb.gui.settings.open");
    }
}
