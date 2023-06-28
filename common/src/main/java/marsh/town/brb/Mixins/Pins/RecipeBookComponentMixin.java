package marsh.town.brb.Mixins.Pins;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

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
