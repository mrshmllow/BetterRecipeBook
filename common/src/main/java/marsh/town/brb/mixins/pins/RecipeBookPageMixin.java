package marsh.town.brb.mixins.pins;

import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.stats.RecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookPage.class)
public class RecipeBookPageMixin {

    @Shadow private RecipeBook recipeBook;

    @Shadow private Minecraft minecraft;

    @Inject(method = "mouseClicked", at = @At(value = "RETURN", target = "Lnet/minecraft/client/gui/screens/recipebook/OverlayRecipeComponent;isVisible()Z"))
    public void mouseClicked(double d, double e, int i, int j, int k, int l, int m, CallbackInfoReturnable<Boolean> cir) {
        // remove search box focus when overlay is clicked
        if (cir.getReturnValue() && minecraft.screen instanceof RecipeUpdateListener rul) {
            EditBox searchBox = ((RecipeBookComponentAccessor) rul.getRecipeBookComponent()).getSearchBox();
            if (searchBox != null) searchBox.setFocused(false);
        }
    }

}
