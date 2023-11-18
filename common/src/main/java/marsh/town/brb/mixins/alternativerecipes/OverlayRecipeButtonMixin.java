package marsh.town.brb.mixins.alternativerecipes;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.mixins.accessors.OverlayRecipeComponentAccessor;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(OverlayRecipeComponent.OverlayRecipeButton.class)
public abstract class OverlayRecipeButtonMixin extends AbstractWidget {

    @Final
    @Shadow
    private boolean isCraftable;
    @Final
    @Shadow
    Recipe<?> recipe;

    @Shadow
    public abstract void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta);

    @Shadow
    @Final
    protected List<OverlayRecipeComponent.OverlayRecipeButton.Pos> ingredientPos;
    @Shadow
    @Final
    OverlayRecipeComponent field_3113;

    public OverlayRecipeButtonMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(at = @At("HEAD"), method = "renderWidget", cancellable = true)
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (((OverlayRecipeComponentAccessor) field_3113).isFurnaceMenu() || !isHoveredOrFocused()) {
            ResourceLocation resourceLocation = BRBTextures.RECIPE_BOOK_PLAIN_OVERLAY_SPRITE.get(this.isCraftable, isHoveredOrFocused());
            gui.blit(resourceLocation, getX(), getY(), 0, 0, this.width, this.height, 24, 24);
            gui.pose().pushPose();
        } else {
            int k = 152;
            if (!this.isCraftable) {
                k += 26;
            }
            int l = 78;
            gui.blit(BRBTextures.RECIPE_BOOK_BACKGROUND_TEXTURE, this.getX(), this.getY(), k, l, this.width, this.height);
            gui.pose().pushPose();
        }

        if (BetterRecipeBook.config.alternativeRecipes.onHover && !this.isHoveredOrFocused()) { // if show alternatives recipe is enabled and recipe is not hovered, show the result item
            ItemStack recipeOutput = this.recipe.getResultItem(field_3113.getRecipeCollection().registryAccess());
            gui.renderItem(recipeOutput, getX() + 4, getY() + 4);
        } else { // otherwise display the crafting recipe
            gui.pose().translate(this.getX() + 2, this.getY() + 2, 150.0);
            for (OverlayRecipeComponent.OverlayRecipeButton.Pos pos : this.ingredientPos) {
                gui.pose().pushPose();
                gui.pose().translate(pos.x, pos.y, 0.0);
                // if furnace menu, keep items at default scale, so it isn't tiny
                if (!((OverlayRecipeComponentAccessor) field_3113).isFurnaceMenu()) {
                    gui.pose().scale(0.375f, 0.375f, 1.0f);
                }
                gui.pose().translate(-8.0, -8.0, 0.0);
                if (pos.ingredients.length > 0) {
                    gui.renderItem(pos.ingredients[Mth.floor(((OverlayRecipeComponentAccessor) field_3113).getTime() / 30.0f) % pos.ingredients.length], 0, 0);
                }
                gui.pose().popPose();
            }
        }
        gui.pose().popPose();

        // blit pin for pinned recipes
        if (BetterRecipeBook.config.enablePinning && BetterRecipeBook.pinnedRecipeManager.pinned.contains(recipe.getId())) {
            gui.pose().pushPose();
            // make sure pin is drawn over the crafting items
            gui.pose().mulPoseMatrix(gui.pose().last().pose());
            gui.blit(BRBTextures.RECIPE_BOOK_OVERLAY_PIN_SPRITE, getX() - 3, getY() - 3, 0, 0, this.width + 3, this.height + 3, 31, 31);
            gui.pose().popPose();
        }

        ci.cancel();
    }

}
