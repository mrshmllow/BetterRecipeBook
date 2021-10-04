package net.marshmallow.BetterRecipeBook.Mixins.InstantCraft;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
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
public abstract class Toggle {
    private static final Identifier BUTTON_TEXTURE = new Identifier("betterrecipebook:textures/gui/buttons.png");
    @Shadow protected MinecraftClient client;

    @Shadow public abstract boolean isOpen();

    @Shadow private int parentHeight;
    @Shadow private int parentWidth;
    @Shadow private int leftOffset;
    protected ToggleButtonWidget instantCraftButton;
    private static final Text TOGGLE_INSTANT_CRAFT_ON_TEXT;
    private static final Text TOGGLE_INSTANT_CRAFT_OFF_TEXT;

    @Inject(method = "reset", at = @At("RETURN"))
    public void reset(CallbackInfo ci) {
        if (!BetterRecipeBook.config.instantCraftModule.showButton) {
            return;
        }

        int i = (this.parentWidth - 147) / 2 - this.leftOffset;
        int j = (this.parentHeight - 166) / 2;

        this.instantCraftButton = new ToggleButtonWidget(i + 110, j + 137, 26, 16, BetterRecipeBook.instantCraftingManager.on);
        if (BetterRecipeBook.config.darkMode) {
            this.instantCraftButton.setTextureUV(0, 36, 28, 18, BUTTON_TEXTURE);
        } else {
            this.instantCraftButton.setTextureUV(0, 0, 28, 18, BUTTON_TEXTURE);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookResults;draw(Lnet/minecraft/client/util/math/MatrixStack;IIIIF)V"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!BetterRecipeBook.config.instantCraftModule.showButton) {
            return;
        }

        this.instantCraftButton.render(matrices, mouseX, mouseY, delta);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.isOpen() && BetterRecipeBook.config.instantCraftModule.showButton) {
            assert this.client.player != null;
            if (!this.client.player.isSpectator()) {
                if (this.instantCraftButton.mouseClicked(mouseX, mouseY, button)) {
                    boolean bl = BetterRecipeBook.instantCraftingManager.toggleOn();
                    this.instantCraftButton.setToggled(bl);
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "drawTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;drawGhostSlotTooltip(Lnet/minecraft/client/util/math/MatrixStack;IIII)V"))
    public void drawTooltip(MatrixStack matrices, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
        if (!BetterRecipeBook.config.instantCraftModule.showButton) {
            return;
        }

        if (this.instantCraftButton.isHovered()) {
            Text text = this.getInstantCraftButtonText();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.renderTooltip(matrices, text, mouseX, mouseY);
            }
        }
    }

    private Text getInstantCraftButtonText() {
        return this.instantCraftButton.isToggled() ? TOGGLE_INSTANT_CRAFT_ON_TEXT : TOGGLE_INSTANT_CRAFT_OFF_TEXT;
    }

    static {
        TOGGLE_INSTANT_CRAFT_ON_TEXT = new TranslatableText("betterrecipebook.gui.instantCraft.on");
        TOGGLE_INSTANT_CRAFT_OFF_TEXT = new TranslatableText("betterrecipebook.gui.instantCraft.off");
    }
}
