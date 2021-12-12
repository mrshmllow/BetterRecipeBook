package net.marshmallow.BetterRecipeBook.Mixins.InstantCraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public abstract class Toggle {
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("betterrecipebook:textures/gui/buttons.png");
    @Shadow protected Minecraft minecraft;

    @Shadow public abstract boolean isVisible();

    @Shadow private int height;
    @Shadow private int width;
    @Shadow private int xOffset;
    protected StateSwitchingButton instantCraftButton;
    private static final Component TOGGLE_INSTANT_CRAFT_ON_TEXT;
    private static final Component TOGGLE_INSTANT_CRAFT_OFF_TEXT;

    @Inject(method = "initVisuals", at = @At("RETURN"))
    public void reset(CallbackInfo ci) {
        if (!BetterRecipeBook.config.instantCraft.showButton) {
            return;
        }

        int i = (this.width - 147) / 2 - this.xOffset;
        int j = (this.height - 166) / 2;

        this.instantCraftButton = new StateSwitchingButton(i + 110, j + 137, 26, 16 + 2, BetterRecipeBook.instantCraftingManager.on);
        if (BetterRecipeBook.config.darkMode) {
            this.instantCraftButton.initTextureValues(0, 36 + 2, 28, 18 + 1, BUTTON_TEXTURE);
        } else {
            this.instantCraftButton.initTextureValues(0, 0, 28, 18 + 1, BUTTON_TEXTURE);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIF)V"))
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!BetterRecipeBook.config.instantCraft.showButton) {
            return;
        }

        this.instantCraftButton.render(matrices, mouseX, mouseY, delta);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.isVisible() && BetterRecipeBook.config.instantCraft.showButton) {
            assert this.minecraft.player != null;
            if (!this.minecraft.player.isSpectator()) {
                if (this.instantCraftButton.mouseClicked(mouseX, mouseY, button)) {
                    boolean bl = BetterRecipeBook.instantCraftingManager.toggleOn();
                    this.instantCraftButton.setStateTriggered(bl);
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;renderGhostRecipeTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;IIII)V"))
    public void drawTooltip(PoseStack matrices, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
        if (!BetterRecipeBook.config.instantCraft.showButton) {
            return;
        }

        if (this.instantCraftButton.isHoveredOrFocused()) {
            Component text = this.getInstantCraftButtonText();
            if (this.minecraft.screen != null) {
                this.minecraft.screen.renderTooltip(matrices, text, mouseX, mouseY);
            }
        }
    }

    private Component getInstantCraftButtonText() {
        return this.instantCraftButton.isStateTriggered() ? TOGGLE_INSTANT_CRAFT_ON_TEXT : TOGGLE_INSTANT_CRAFT_OFF_TEXT;
    }

    static {
        TOGGLE_INSTANT_CRAFT_ON_TEXT = new TranslatableComponent("betterrecipebook.gui.instantCraft.on");
        TOGGLE_INSTANT_CRAFT_OFF_TEXT = new TranslatableComponent("betterrecipebook.gui.instantCraft.off");
    }
}
