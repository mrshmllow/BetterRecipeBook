package net.marshmallow.BetterRecipeBook.Mixins.UnGroup;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Locale;

@Mixin(RecipeBookComponent.class)
public class Search {
    @Shadow private String lastSearch;

    @Inject(method = "updateCollections", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;<init>(Ljava/util/Collection;)V"))
    private void refreshSearchResults(boolean arg0, CallbackInfo ci, List<RecipeCollection> list, List<RecipeCollection> list2, String string) {
        if (BetterRecipeBook.config.alternativeRecipes.noGrouped) {
            list2.removeIf((recipeResultCollection) -> {
                for (Recipe<?> recipe : recipeResultCollection.getRecipes()) {
                    return !recipe.getResultItem().getHoverName().getString().toLowerCase(Locale.ROOT).contains(this.lastSearch.toLowerCase(Locale.ROOT));
                }
                return false;
            });
        }
    }

    //@Inject(method = "method_2594(Lit/unimi/dsi/fastutil/objects/ObjectSet;Lnet/minecraft/client/gui/screen/recipebook/RecipeResultCollection;)Z", at = @At("RETURN"), cancellable = true)
    //private static void cancelContains(ObjectSet resultCollection, RecipeCollection recipeResultCollection, CallbackInfoReturnable<Boolean> cir) {
    //    if (BetterRecipeBook.config.alternativeRecipes.noGrouped) {
    //        cir.setReturnValue(false);
    //    }
    //}
}