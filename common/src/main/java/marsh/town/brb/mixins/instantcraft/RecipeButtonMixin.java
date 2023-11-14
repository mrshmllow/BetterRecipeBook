package marsh.town.brb.mixins.instantcraft;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(RecipeButton.class)
public class RecipeButtonMixin {

    @Shadow
    private RecipeCollection collection;

    @Inject(method = "getOrderedRecipes", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void getOrderedRecipes(CallbackInfoReturnable<List<Recipe<?>>> cir, List<Recipe<?>> recipes) {
        // fixes division by zero due to zero list size when keeping instant craft recipes stationary
        if (this.collection == BetterRecipeBook.currentHoveredRecipeCollection && recipes.isEmpty()) {
            cir.setReturnValue(new ArrayList<>(collection.getDisplayRecipes(false)));
        }
    }

    @Inject(method = "renderWidget", at = @At(value = "HEAD"))
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {

    }

}
