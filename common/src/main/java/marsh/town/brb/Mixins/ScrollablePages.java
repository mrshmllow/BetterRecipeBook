package marsh.town.brb.Mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookPage.class)
public abstract class ScrollablePages {
    @Shadow
    private int currentPage;
    @Shadow
    private int totalPages;
    @Shadow
    protected abstract void updateButtonsForPage();
    @Shadow
    private StateSwitchingButton forwardButton;
    @Shadow
    private StateSwitchingButton backButton;

    @Inject(at = @At("HEAD"), method = "mouseClicked")
    public void mouseClickedHead(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight, CallbackInfoReturnable<Boolean> cir) {
        if (BetterRecipeBook.config.scrolling.enableScrolling) {
            if (forwardButton.mouseClicked(mouseX, mouseY, button)) {
                if (currentPage >= totalPages - 1) {
                    currentPage = -1;
                }
            } else if (backButton.mouseClicked(mouseX, mouseY, button)) {
                if (currentPage <= 0) {
                    currentPage = totalPages;
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "render")
    public void draw(PoseStack matrixStack, int i, int j, int k, int l, float f, CallbackInfo ci) {
        if (BetterRecipeBook.queuedScroll != 0 && BetterRecipeBook.config.scrolling.enableScrolling) {
            int queuedPage = BetterRecipeBook.queuedScroll + currentPage;

            if (queuedPage <= totalPages - 1 && queuedPage >= 0) {
                currentPage += BetterRecipeBook.queuedScroll;
            } else if (BetterRecipeBook.config.scrolling.scrollAround) {
                if (queuedPage < 0) {
                    currentPage = totalPages - 1;
                } else if (queuedPage > totalPages - 1) {
                    currentPage = 0;
                }
            }

            updateButtonsForPage();
            BetterRecipeBook.queuedScroll = 0;
        }
    }

    @Inject(at = @At("HEAD"), method = "updateButtonsForPage", cancellable = true)
    public void avoidIndexOutOfBounds(CallbackInfo ci) {
        if (totalPages == 0 && currentPage == -1) {
            currentPage = 0;
            ci.cancel();
        }
    }

    @Inject(at = @At("RETURN"), method = "init")
    public void initialize(Minecraft minecraftClient, int parentLeft, int parentTop, CallbackInfo ci) {
        BetterRecipeBook.queuedScroll = 0;
    }

    /**
     * @author marshmallow
     */
    @Overwrite
    void updateArrowButtons() {
        if (BetterRecipeBook.config.scrolling.scrollAround && BetterRecipeBook.config.scrolling.enableScrolling && !(totalPages < 1)) {
            forwardButton.visible = true;
            backButton.visible = true;
        } else {
            forwardButton.visible = totalPages > 1 && currentPage < totalPages - 1;
            backButton.visible = totalPages > 1 && currentPage > 0;
        }
    }
}
