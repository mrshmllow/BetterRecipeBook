package net.marshmallow.BetterRecipeBook.Mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(RecipeAlternativesWidget.AlternativeButtonWidget.class)
public abstract class AlternativeRecipes extends ClickableWidget implements RecipeGridAligner<Ingredient> {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("betterrecipebook:textures/gui/alt_button_blank.png");

    @Final @Shadow
    private boolean craftable;
    @Final @Shadow
    private Recipe<?> recipe;

    @Shadow public abstract void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta);

    public AlternativeRecipes(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }


    @Inject(at = @At("HEAD"), method = "renderButton", cancellable = true)
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!this.isHovered() && BetterRecipeBook.config.showAlternativesOnHover) {
            RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
            matrices.push();

            int i = 0;
            int j = 0;

            if (!craftable) {
                i += 26;
            }

            this.drawTexture(matrices, this.x, this.y, i, j, this.width, this.height);

            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            ItemStack recipeOutput = this.recipe.getOutput();

            MatrixStack s = RenderSystem.getModelViewStack();
            s.push();
            s.method_34425(matrices.peek().getModel().copy()); // No idea what this does
            itemRenderer.renderInGuiWithOverrides(recipeOutput, this.x + 4, this.y + 4); // Why do we do this twice?
            itemRenderer.renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, recipeOutput, this.x + 4, this.y + 4); // ^
            RenderSystem.enableDepthTest();
            s.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.disableDepthTest();

            ci.cancel();
        }
    }
}
