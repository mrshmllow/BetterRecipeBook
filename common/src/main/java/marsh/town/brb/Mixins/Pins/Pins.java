package marsh.town.brb.Mixins.Pins;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.Mixins.Accessors.AlternativeButtonWidgetAccessor;
import marsh.town.brb.Mixins.Accessors.RecipeAlternativesWidgetAccessor;
import marsh.town.brb.Mixins.Accessors.RecipeBookResultsAccessor;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.*;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;
import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class Pins {
    @Shadow protected Minecraft minecraft;
    @Nullable @Shadow private EditBox searchBox;
    @Final @Shadow private RecipeBookPage recipeBookPage;

    @Shadow protected abstract void updateCollections(boolean resetCurrentPage);

    @Shadow private ClientRecipeBook book;

    @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
    public void add(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.searchBox == null) return;
        if (!BetterRecipeBook.config.enablePinning) return;

        OverlayRecipeComponent alternatesWidget = ((RecipeBookResultsAccessor) this.recipeBookPage).getOverlay();

        if (keyCode == GLFW.GLFW_KEY_F) {
            List<OverlayRecipeComponent.OverlayRecipeButton> alternativeButtons = ((RecipeAlternativesWidgetAccessor) alternatesWidget).getRecipeButtons();
            for (OverlayRecipeComponent.OverlayRecipeButton alternativeButton : alternativeButtons) {
                if (alternativeButton.isHoveredOrFocused()) {
                    RecipeCollection recipeResultCollection = new RecipeCollection(Collections.singletonList(((AlternativeButtonWidgetAccessor) alternativeButton).getRecipe()));
                    recipeResultCollection.updateKnownRecipes(this.book);
                    BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavourite(recipeResultCollection);
                    this.updateCollections(false);
                    cir.setReturnValue(true);
                    return;
                }
            }

            if (!this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
                for (RecipeButton resultButton : ((RecipeBookResultsAccessor) this.recipeBookPage).getButtons()) {
                    if (resultButton.isHoveredOrFocused()) {
                        BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavourite(resultButton.getCollection());
                        this.updateCollections(false);
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

    @Inject(method = "updateCollections", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;Z)V"))
    private void sort(boolean bl, CallbackInfo ci, List<RecipeCollection> list, List<RecipeCollection> list2) {
        if (!BetterRecipeBook.config.enablePinning) return;

        List<RecipeCollection> list3 = Lists.newArrayList(list2);

        for (RecipeCollection recipeResultCollection : list3) {
            if (BetterRecipeBook.pinnedRecipeManager.has(recipeResultCollection)) {
                list2.remove(recipeResultCollection);
                list2.add(0, recipeResultCollection);
            }
        }

    }
}