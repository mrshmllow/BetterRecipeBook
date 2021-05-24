package net.marshmallow.BetterRecipeBook.mixin.RecipeAlternativesWidget;

import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RecipeAlternativesWidget.AlternativeButtonWidget.class)
public interface AlternativeButtonWidgetAccessor {
    @Accessor("recipe")
    Recipe<?> getRecipe();
}
