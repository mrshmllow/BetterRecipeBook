package marsh.town.brb.mixins.pins;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.PinnedRecipeManager;
import marsh.town.brb.mixins.accessors.OverlayRecipeButtonAccessor;
import marsh.town.brb.mixins.accessors.OverlayRecipeComponentAccessor;
import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
import marsh.town.brb.mixins.accessors.RecipeBookPageAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
    public void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof RecipeUpdateListener rul)) return;

        Minecraft minecraft = Minecraft.getInstance();
        RecipeBookComponent book = rul.getRecipeBookComponent();

        if (!book.isVisible()) return;

        RecipeBookPage page = ((RecipeBookComponentAccessor) book).getRecipeBookPage();
        OverlayRecipeComponent alternatesWidget = ((RecipeBookPageAccessor) page).getOverlay();

        EditBox searchBox = ((RecipeBookComponentAccessor) book).getSearchBox();

        // when F is pressed, handle pinning/unpinning of recipes except when searchBox is consuming input
        if (BetterRecipeBook.config.enablePinning && BetterRecipeBook.PIN_MAPPING.matches(keyCode, scanCode) && !searchBox.canConsumeInput()) {
            // handle alternatives widget first
            if (alternatesWidget.isVisible()) {
                for (OverlayRecipeComponent.OverlayRecipeButton alternativeButton : ((OverlayRecipeComponentAccessor) alternatesWidget).getRecipeButtons()) {
                    if (alternativeButton.isHoveredOrFocused()) {
                        PinnedRecipeManager.handlePinRecipe(book, page, ((OverlayRecipeButtonAccessor) alternativeButton).getRecipe());
                        cir.setReturnValue(true);
                        return;
                    }
                }
                return;
            }

            for (RecipeButton button : ((RecipeBookPageAccessor) page).getButtons()) {
                if (button.isHoveredOrFocused()) {
                    PinnedRecipeManager.handlePinRecipe(book, page, button.getRecipe());
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        // when <chat key> is pressed, focus recipes component for searchBox
        // this also works for BrewingRecipeBookComponent as the super's searchBox is set to the same object
        if (minecraft.options.keyChat.matches(keyCode, scanCode)) {
            minecraft.screen.setFocused(book);
        }

    }

}