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

import java.util.List;


@Mixin(RecipeAlternativesWidget.AlternativeButtonWidget.class)
public abstract class AlternativeRecipes extends ClickableWidget implements RecipeGridAligner<Ingredient> {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("betterrecipebook:textures/gui/alt_button_blank.png");

    @Final @Shadow
    private boolean craftable;
    @Final @Shadow
    Recipe<?> recipe;
    @Shadow public abstract void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta);
    @Shadow @Final protected List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> slots;

    public AlternativeRecipes(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Inject(at = @At("HEAD"), method = "renderButton", cancellable = true)
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int i = 0;
        int j = 0;

        if (BetterRecipeBook.config.darkMode) {
            j = 52;
        }

        if (!craftable) {
            i += 26;
        }

        if (this.slots.size() == 1) {
            matrices.push();
            this.drawTexture(matrices, this.x, this.y, i, j, this.width, this.height);
            MatrixStack matrixStack = RenderSystem.getModelViewStack();

            matrixStack.push();
            matrixStack.method_34425(matrices.peek().getModel().copy());
            MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(this.slots.get(0).stacks[0], this.x + 4, this.y + 4);
            RenderSystem.enableDepthTest();
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();

            matrices.pop();
            ci.cancel();
        } else if (!this.isHovered() && BetterRecipeBook.config.showAlternativesOnHover) {
            matrices.push();
            this.drawTexture(matrices, this.x, this.y, i, j, this.width, this.height);

            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            ItemStack recipeOutput = this.recipe.getOutput();

            MatrixStack matrixStack = RenderSystem.getModelViewStack();

            matrixStack.push();
            matrixStack.method_34425(matrices.peek().getModel().copy());
            itemRenderer.renderInGuiWithOverrides(recipeOutput, this.x + 4, this.y + 4);
            RenderSystem.enableDepthTest();
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();

            matrices.pop();
            ci.cancel();
        }
    }
}
