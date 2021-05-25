package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(RecipeBookResults.class)
public abstract class RecipeBookResultsMixin {
    @Shadow
    private int currentPage;
    @Shadow
    private int pageCount;
    @Shadow
    protected abstract void refreshResultButtons();
    @Final @Shadow
    private RecipeAlternativesWidget alternatesWidget;
    @Shadow
    private ToggleButtonWidget nextPageButton;
    @Shadow
    private ToggleButtonWidget prevPageButton;
    @Final @Shadow
    private List<AnimatedResultButton> resultButtons;

    @Inject(at = @At("HEAD"), method = "mouseClicked")
    public void mouseClickedHead(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight, CallbackInfoReturnable<Boolean> cir) {
        if (BetterRecipeBook.config.scrollingModule.enableScrolling) {
            if (nextPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (currentPage >= pageCount - 1) {
                    currentPage = -1;
                }
            } else if (prevPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (currentPage <= 0) {
                    currentPage = pageCount;
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "mouseClicked")
    public void mouseClickedReturn(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight, CallbackInfoReturnable<Boolean> cir) {
        if (!alternatesWidget.isVisible() && !nextPageButton.mouseClicked(mouseX, mouseY, button) && !prevPageButton.mouseClicked(mouseX, mouseY, button) && BetterRecipeBook.config.enabledCheating) {
            if (button == 0) {
                Iterator<AnimatedResultButton> iterator = resultButtons.iterator();

                AnimatedResultButton animatedResultButton;
                do {
                    if (!iterator.hasNext()) {
                        return;
                    }

                    animatedResultButton = iterator.next();
                } while(!animatedResultButton.mouseClicked(mouseX, mouseY, button));

                List<Recipe<?>> recipes = ((AnimatedResultButtonAccessor) animatedResultButton).results().getRecipes(false);

                if (recipes.size() == 1) {
                    BetterRecipeBook.cheat(recipes.get(0).getOutput().getItem());
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "draw")
    public void draw(MatrixStack matrixStack, int i, int j, int k, int l, float f, CallbackInfo ci) {
        if (BetterRecipeBook.queuedScroll != 0 && BetterRecipeBook.config.scrollingModule.enableScrolling) {
            int queuedPage = BetterRecipeBook.queuedScroll + currentPage;

            if (queuedPage <= pageCount - 1 && queuedPage >= 0) {
                currentPage += BetterRecipeBook.queuedScroll;
            } else if (BetterRecipeBook.config.scrollingModule.scrollAround) {
                if (queuedPage < 0) {
                    currentPage = pageCount - 1;
                } else if (queuedPage > pageCount - 1) {
                    currentPage = 0;
                }
            }

            refreshResultButtons();
            BetterRecipeBook.queuedScroll = 0;
        }
    }

    @Inject(at = @At("RETURN"), method = "initialize")
    public void initialize(MinecraftClient minecraftClient, int parentLeft, int parentTop, CallbackInfo ci) {
        BetterRecipeBook.queuedScroll = 0;
    }

    /**
     * @author marshmallow
     */
    @Overwrite
    public void hideShowPageButtons() {
        if (BetterRecipeBook.config.scrollingModule.scrollAround && !(pageCount <= 1)) {
            nextPageButton.visible = true;
            prevPageButton.visible = true;
        } else {
            nextPageButton.visible = pageCount > 1 && currentPage < pageCount - 1;
            prevPageButton.visible = pageCount > 1 && currentPage > 0;
        }
    }
}
