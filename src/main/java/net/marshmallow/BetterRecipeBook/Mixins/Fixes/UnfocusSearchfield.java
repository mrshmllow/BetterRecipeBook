package net.marshmallow.BetterRecipeBook.Mixins.Fixes;

import net.marshmallow.BetterRecipeBook.Mixins.Accessors.RecipeBookResultsAccessor;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class UnfocusSearchfield {
    @Shadow @Nullable private TextFieldWidget searchField;

    @Shadow @Final private RecipeBookResults recipesArea;

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void closeOnInput(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.searchField != null) {
            if (this.searchField.mouseClicked(mouseX, mouseY, button)) {
                ((RecipeBookResultsAccessor) this.recipesArea).getAlternatesWidget().setVisible(false);
            }
        }
    }
}
