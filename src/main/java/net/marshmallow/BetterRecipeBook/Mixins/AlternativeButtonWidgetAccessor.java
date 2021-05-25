package net.marshmallow.BetterRecipeBook.Mixins;

import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeAlternativesWidget.AlternativeButtonWidget.class)
public interface AlternativeButtonWidgetAccessor {
    @Accessor("recipe")
    Recipe<?> getRecipe();
}
