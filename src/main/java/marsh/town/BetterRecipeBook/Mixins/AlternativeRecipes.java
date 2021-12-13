package marsh.town.BetterRecipeBook.Mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import marsh.town.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(OverlayRecipeComponent.OverlayRecipeButton.class)
public abstract class AlternativeRecipes extends AbstractWidget implements PlaceRecipe<Ingredient> {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("betterrecipebook:textures/gui/alt_button_blank.png");

    @Final @Shadow
    private boolean isCraftable;
    @Final @Shadow
    Recipe<?> recipe;
    @Shadow public abstract void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta);
    @Shadow @Final protected List<OverlayRecipeComponent.OverlayRecipeButton.Pos> ingredientPos;

    public AlternativeRecipes(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(at = @At("HEAD"), method = "renderButton", cancellable = true)
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int i = 0;
        int j = 0;

        if (BetterRecipeBook.config.darkMode) {
            j = 52;
        }

        if (!isCraftable) {
            i += 26;
        }

        if (this.ingredientPos.size() == 1 && this.isHoveredOrFocused()) {
            matrices.pushPose();
            this.blit(matrices, this.x, this.y, i, j, this.width, this.height);
            PoseStack matrixStack = RenderSystem.getModelViewStack();

            matrixStack.pushPose();
            matrixStack.mulPoseMatrix(matrices.last().pose().copy()); // No idea what this does
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(this.ingredientPos.get(0).ingredients[0], this.x + 4, this.y + 4);
            RenderSystem.enableDepthTest();
            matrixStack.popPose();
            RenderSystem.applyModelViewMatrix();

            matrices.popPose();
            ci.cancel();
        } else if (!this.isHoveredOrFocused() && BetterRecipeBook.config.alternativeRecipes.onHover) {
            matrices.pushPose();
            this.blit(matrices, this.x, this.y, i, j, this.width, this.height);

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            ItemStack recipeOutput = this.recipe.getResultItem();

            PoseStack matrixStack = RenderSystem.getModelViewStack();

            matrixStack.pushPose();
            matrixStack.mulPoseMatrix(matrices.last().pose().copy()); // No idea what this does
            itemRenderer.renderAndDecorateItem(recipeOutput, this.x + 4, this.y + 4);
            RenderSystem.enableDepthTest();
            matrixStack.popPose();
            RenderSystem.applyModelViewMatrix();

            matrices.popPose();
            ci.cancel();
        }
    }
}
