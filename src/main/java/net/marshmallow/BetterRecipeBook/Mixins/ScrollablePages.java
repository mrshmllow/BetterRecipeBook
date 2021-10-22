package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookResults.class)
public abstract class ScrollablePages {
    @Shadow
    private int currentPage;
    @Shadow
    private int pageCount;
    @Shadow
    protected abstract void refreshResultButtons();
    @Shadow
    private ToggleButtonWidget nextPageButton;
    @Shadow
    private ToggleButtonWidget prevPageButton;

    @Inject(at = @At("HEAD"), method = "mouseClicked")
    public void mouseClickedHead(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight, CallbackInfoReturnable<Boolean> cir) {
        if (BetterRecipeBook.config.scrolling.enableScrolling) {
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

    @Inject(at = @At("HEAD"), method = "draw")
    public void draw(MatrixStack matrixStack, int i, int j, int k, int l, float f, CallbackInfo ci) {
        if (BetterRecipeBook.queuedScroll != 0 && BetterRecipeBook.config.scrolling.enableScrolling) {
            int queuedPage = BetterRecipeBook.queuedScroll + currentPage;

            if (queuedPage <= pageCount - 1 && queuedPage >= 0) {
                currentPage += BetterRecipeBook.queuedScroll;
            } else if (BetterRecipeBook.config.scrolling.scrollAround) {
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

    @Inject(at = @At("HEAD"), method = "refreshResultButtons", cancellable = true)
    public void avoidIndexOutOfBounds(CallbackInfo ci) {
        if (pageCount == 0 && currentPage == -1) {
            currentPage = 0;
            ci.cancel();
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
    void hideShowPageButtons() {
        if (BetterRecipeBook.config.scrolling.scrollAround && !(pageCount < 1)) {
            nextPageButton.visible = true;
            prevPageButton.visible = true;
        } else {
            nextPageButton.visible = pageCount > 1 && currentPage < pageCount - 1;
            prevPageButton.visible = pageCount > 1 && currentPage > 0;
        }
    }
}
