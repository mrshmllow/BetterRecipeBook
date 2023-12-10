package marsh.town.brb.mixins.instantcraft;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.RecipeHolder;
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
    @Shadow private RecipeBook book;

    @Unique boolean betterRecipeBook$initialHover = false;

    @Inject(method = "getOrderedRecipes", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void getOrderedRecipes(CallbackInfoReturnable<List<RecipeHolder<?>>> cir, List<RecipeHolder<?>> list) {
        if (betterRecipeBook$initialHover && ((RecipeButton) (Object) this).isHovered()) {
            cir.setReturnValue(new ArrayList<>(List.of(BetterRecipeBook.instantCraftingManager.lastClickedRecipe)));
        } else {
            betterRecipeBook$initialHover = false;
            if (list.isEmpty()) {
                cir.setReturnValue(collection.getDisplayRecipes(false));
            }
        }
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        betterRecipeBook$initialHover = ((RecipeButton) (Object) this).isHovered();
    }

}
