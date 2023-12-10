package marsh.town.brb.mixins.pins;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.pins.PinnableRecipeCollection;
import marsh.town.brb.interfaces.IPinningComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookComponentMixin implements IPinningComponent<PinnableRecipeCollection> {

    @Shadow
    @Final
    private RecipeBookPage recipeBookPage;

    @Unique
    public void betterRecipeBook$sortByPinsInPlaceCollection(List<RecipeCollection> results) {
        List<RecipeCollection> tempResults = Lists.newArrayList(results);

        if (BetterRecipeBook.config.enablePinning) {
            for (RecipeCollection result : tempResults) {
                if (BetterRecipeBook.pinnedRecipeManager.has(PinnableRecipeCollection.of(result))) {
                    results.remove(result);
                    results.add(0, result);
                }
            }
        }
    }

    @ModifyArg(method = "updateCollections", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;Z)V"))
    private List<RecipeCollection> updateCollections(List<RecipeCollection> list) {
        this.betterRecipeBook$sortByPinsInPlaceCollection(list);
        return list;
    }
}
