package net.marshmallow.BetterRecipeBook.Mixins.UnGroup;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Locale;

@Mixin(RecipeBookWidget.class)
public class Search {
    @Shadow private String searchText;

    @Inject(method = "refreshResults", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;<init>(Ljava/util/Collection;)V"))
    private void refreshSearchResults(boolean arg0, CallbackInfo ci, List<RecipeResultCollection> list, List<RecipeResultCollection> list2, String string) {
        if (BetterRecipeBook.config.alternativeRecipes.noGrouped) {
            list2.removeIf((recipeResultCollection) -> {
                for (Recipe<?> recipe : recipeResultCollection.getAllRecipes()) {
                    return !recipe.getOutput().getName().getString().toLowerCase(Locale.ROOT).contains(this.searchText.toLowerCase(Locale.ROOT));
                }
                return false;
            });
        }
    }

    @Inject(method = "method_2594(Lit/unimi/dsi/fastutil/objects/ObjectSet;Lnet/minecraft/client/gui/screen/recipebook/RecipeResultCollection;)Z", at = @At("RETURN"), cancellable = true)
    private static void cancelContains(ObjectSet resultCollection, RecipeResultCollection recipeResultCollection, CallbackInfoReturnable<Boolean> cir) {
        if (BetterRecipeBook.config.alternativeRecipes.noGrouped) {
            cir.setReturnValue(false);
        }
    }
}