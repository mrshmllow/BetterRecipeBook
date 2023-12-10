package marsh.town.brb.mixins.instantcraft;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
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

    @Shadow protected Minecraft minecraft;
    @Shadow private int height;
    @Shadow private int width;
    @Shadow private int xOffset;
    @Shadow @Final private RecipeBookPage recipeBookPage;

    @Shadow public abstract boolean isVisible();

    @Unique protected StateSwitchingButton betterRecipeBook$instantCraftButton;
    @Unique private static final Component TOGGLE_INSTANT_CRAFT_ON_TEXT;
    @Unique private static final Component TOGGLE_INSTANT_CRAFT_OFF_TEXT;

    @Unique
    private boolean betterRecipeBook$shouldSkip() {
        if (!BetterRecipeBook.config.instantCraft.showButton) {
            return true;
        }

        // remove instant craft button in furnaces
        return ((Object) this) instanceof AbstractFurnaceRecipeBookComponent;
    }

    @Inject(method = "initVisuals", at = @At("RETURN"))
    public void reset(CallbackInfo ci) {
        if (betterRecipeBook$shouldSkip()) {
            return;
        }

        int i = (this.width - 147) / 2 - this.xOffset;
        int j = (this.height - 166) / 2;

        this.betterRecipeBook$instantCraftButton = new StateSwitchingButton(i + 110, j + 137, 26, 16 + 2, BetterRecipeBook.instantCraftingManager.isEnabled());
        BetterRecipeBook.instantCraftingManager.lastInstantCraftButton = this.betterRecipeBook$instantCraftButton;
        this.betterRecipeBook$instantCraftButton.initTextureValues(BRBTextures.RECIPE_BOOK_INSTANT_CRAFT_BUTTON_SPRITES);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;render(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V"))
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (betterRecipeBook$shouldSkip()) {
            return;
        }

        this.betterRecipeBook$instantCraftButton.render(gui, mouseX, mouseY, delta);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.isVisible() && !betterRecipeBook$shouldSkip()) {
            if (this.betterRecipeBook$instantCraftButton.mouseClicked(mouseX, mouseY, button)) {
                boolean bl = BetterRecipeBook.instantCraftingManager.toggleEnabled();
                this.betterRecipeBook$instantCraftButton.setStateTriggered(bl);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;renderGhostRecipeTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"))
    public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
        if (betterRecipeBook$shouldSkip()) {
            return;
        }

        if (this.betterRecipeBook$instantCraftButton.isHoveredOrFocused()) {
            Component text = this.betterRecipeBook$instantCraftButton.isStateTriggered() ? TOGGLE_INSTANT_CRAFT_ON_TEXT : TOGGLE_INSTANT_CRAFT_OFF_TEXT;
            if (this.minecraft.screen != null) {
                gui.renderComponentTooltip(minecraft.font, List.of(text), mouseX, mouseY);
            }
        }
    }

    static {
        TOGGLE_INSTANT_CRAFT_ON_TEXT = Component.translatable("brb.gui.instantCraft.on");
        TOGGLE_INSTANT_CRAFT_OFF_TEXT = Component.translatable("brb.gui.instantCraft.off");
    }
}
