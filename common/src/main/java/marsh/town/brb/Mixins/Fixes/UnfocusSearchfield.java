package marsh.town.brb.Mixins.Fixes;

import marsh.town.brb.Mixins.Accessors.RecipeBookResultsAccessor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class UnfocusSearchfield {
    @Shadow @Nullable private EditBox searchBox;

    @Shadow @Final private RecipeBookPage recipeBookPage;

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void closeOnInput(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.searchBox != null) {
            if (this.searchBox.mouseClicked(mouseX, mouseY, button)) {
                ((RecipeBookResultsAccessor) this.recipeBookPage).getOverlay().setVisible(false);
            }
        }
    }
}
