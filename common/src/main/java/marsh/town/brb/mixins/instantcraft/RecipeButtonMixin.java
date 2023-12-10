package marsh.town.brb.mixins.instantcraft;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(RecipeButton.class)
public class RecipeButtonMixin {

    @Shadow private RecipeCollection collection;

    @Unique private List<Recipe<?>> betterRecipeBook$lastClicked;

    @Inject(method = "getOrderedRecipes", at = @At(value = "RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void getOrderedRecipes(CallbackInfoReturnable<List<Recipe<?>>> cir, List<Recipe<?>> holders) {
        if (holders.isEmpty()) {
            cir.setReturnValue(new ArrayList<>(betterRecipeBook$lastClicked));
        }
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    public void init(RecipeCollection collection, RecipeBookPage recipeBookPage, CallbackInfo ci) {
        if (BetterRecipeBook.instantCraftingManager.lastHoveredCollection == collection) {
            BetterRecipeBook.instantCraftingManager.lastHoveredCollection = null;
            betterRecipeBook$lastClicked = List.of(BetterRecipeBook.instantCraftingManager.lastClickedRecipe);
        }
    }

}
