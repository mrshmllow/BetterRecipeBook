package net.marshmallow.BetterRecipeBook.Mixins;

import com.google.common.collect.Lists;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mixin(ClientRecipeBook.class)
public class SplitGrouped extends RecipeBook {
    @Shadow private Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup;

    @Inject(method = "getResultsForGroup", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("RETURN"), cancellable = true)
    private void split(RecipeBookGroup category, CallbackInfoReturnable<List<RecipeResultCollection>> cir) {
        if (BetterRecipeBook.config.alternativeRecipes.noGrouped) {
            List<RecipeResultCollection> list = Lists.newArrayList(this.resultsByGroup.getOrDefault(category, Collections.emptyList()));
            List<RecipeResultCollection> list2 = Lists.newArrayList(list);

            for (RecipeResultCollection recipeResultCollection : list) {
                if (recipeResultCollection.getAllRecipes().size() > 1) {
                    List<Recipe<?>> recipes = recipeResultCollection.getAllRecipes();
                    list2.remove(recipeResultCollection);

                    for (Recipe<?> recipe : recipes) {
                        RecipeResultCollection recipeResultCollection1 = new RecipeResultCollection(Collections.singletonList(recipe));
                        recipeResultCollection1.initialize(this);

                        list2.add(recipeResultCollection1);
                    }
                }
            }
            cir.setReturnValue(list2);
        }
    }
}
