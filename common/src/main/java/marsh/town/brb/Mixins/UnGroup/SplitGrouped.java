package marsh.town.brb.Mixins.UnGroup;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;
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
    @Shadow private Map<RecipeBookCategories, List<RecipeCollection>> collectionsByTab;

    @Inject(method = "getCollection", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("RETURN"), cancellable = true)
    private void split(RecipeBookCategories category, CallbackInfoReturnable<List<RecipeCollection>> cir) {
        if (BetterRecipeBook.config.alternativeRecipes.noGrouped) {
            List<RecipeCollection> list = Lists.newArrayList(this.collectionsByTab.getOrDefault(category, Collections.emptyList()));
            List<RecipeCollection> list2 = Lists.newArrayList(list);

            for (RecipeCollection recipeResultCollection : list) {
                if (recipeResultCollection.getRecipes().size() > 1) {
                    List<Recipe<?>> recipes = recipeResultCollection.getRecipes();
                    list2.remove(recipeResultCollection);

                    for (Recipe<?> recipe : recipes) {
                        RecipeCollection recipeResultCollection1 = new RecipeCollection(recipeResultCollection.registryAccess(), Collections.singletonList(recipe));
                        recipeResultCollection1.updateKnownRecipes(this);

                        list2.add(recipeResultCollection1);
                    }
                }
            }
            cir.setReturnValue(list2);
        }
    }
}
