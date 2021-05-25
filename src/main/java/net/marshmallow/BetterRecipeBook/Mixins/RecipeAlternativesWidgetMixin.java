package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(RecipeAlternativesWidget.class)
public class RecipeAlternativesWidgetMixin {
    @Final @Shadow
    private List<RecipeAlternativesWidget.AlternativeButtonWidget> alternativeButtons;

    @Inject(at = @At("HEAD"), method = "mouseClicked")
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (BetterRecipeBook.config.enabledCheating) {
            Iterator<RecipeAlternativesWidget.AlternativeButtonWidget> var6 = alternativeButtons.iterator();

            RecipeAlternativesWidget.AlternativeButtonWidget alternativeButtonWidget;
            do {
                if (!var6.hasNext()) {
                    return;
                }

                alternativeButtonWidget = var6.next();
            } while(!alternativeButtonWidget.mouseClicked(mouseX, mouseY, button));

            Recipe<?> recipe = ((AlternativeButtonWidgetAccessor) alternativeButtonWidget).recipe();

            BetterRecipeBook.cheat(recipe.getOutput().getItem());
        }
    }
}
