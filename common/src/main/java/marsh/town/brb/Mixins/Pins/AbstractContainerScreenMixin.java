package marsh.town.brb.Mixins.Pins;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.BrewingStand.BrewingRecipeBookComponent;
import marsh.town.brb.Mixins.Accessors.OverlayRecipeButtonAccessor;
import marsh.town.brb.Mixins.Accessors.OverlayRecipeComponentAccessor;
import marsh.town.brb.Mixins.Accessors.RecipeBookComponentAccessor;
import marsh.town.brb.Mixins.Accessors.RecipeBookPageAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.*;
import net.minecraft.world.item.crafting.Recipe;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
    public void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!BetterRecipeBook.config.enablePinning || !(this instanceof RecipeUpdateListener rul)) return;

        Minecraft minecraft = Minecraft.getInstance();
        RecipeBookComponent book = rul.getRecipeBookComponent();

        RecipeBookPage page = ((RecipeBookComponentAccessor) book).getRecipeBookPage();
        OverlayRecipeComponent alternatesWidget = ((RecipeBookPageAccessor) page).getOverlay();

        EditBox searchBox = ((RecipeBookComponentAccessor) book).getSearchBox();
        if (searchBox == null || !book.isVisible() && !(book instanceof BrewingRecipeBookComponent b && b.isOpen())) return;

        // when F is pressed, handle pinning/unpinning of recipes except when searchBox is consuming input
        if (keyCode == GLFW.GLFW_KEY_F && !searchBox.canConsumeInput()) {
            // handle alternatives widget first
            for (OverlayRecipeComponent.OverlayRecipeButton alternativeButton : ((OverlayRecipeComponentAccessor) alternatesWidget).getRecipeButtons()) {
                if (alternativeButton.isHoveredOrFocused()) {
                    handlePinRecipe(book, page, ((OverlayRecipeButtonAccessor) alternativeButton).getRecipe());
                    cir.setReturnValue(true);
                    return;
                }
            }

            // handle recipes page
            if (book instanceof BrewingRecipeBookComponent brewingBook && brewingBook.keyPressed(keyCode, scanCode, modifiers)) {
                // BrewingRecipeBookComponent handles pinning by itself
                cir.setReturnValue(true);
                return;
            } else {
                for (RecipeButton button : ((RecipeBookPageAccessor) page).getButtons()) {
                    if (button.isHoveredOrFocused()) {
                        handlePinRecipe(book, page, button.getRecipe());
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }

        }

        // when <chat key> is pressed, focus recipes component for searchBox
        // this also works for BrewingRecipeBookComponent as the super's searchBox is set to the same object
        if (minecraft.options.keyChat.matches(keyCode, scanCode)) {
            minecraft.screen.setFocused(book);
        }

    }

    private static void handlePinRecipe(RecipeBookComponent book, RecipeBookPage page, Recipe<?> recipe) {
        RecipeCollection recipeResultCollection = new RecipeCollection(Minecraft.getInstance().level.registryAccess(), Collections.singletonList(recipe));
        recipeResultCollection.updateKnownRecipes(page.getRecipeBook());
        BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavourite(recipeResultCollection);
        ((RecipeBookComponentAccessor) book).updateCollectionsInvoker(false);
    }

}