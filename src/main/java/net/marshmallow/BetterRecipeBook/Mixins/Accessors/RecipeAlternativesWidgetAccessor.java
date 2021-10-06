package net.marshmallow.BetterRecipeBook.Mixins.Accessors;

import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RecipeAlternativesWidget.class)
public interface RecipeAlternativesWidgetAccessor {
    @Accessor()
    List<RecipeAlternativesWidget.AlternativeButtonWidget> getAlternativeButtons();
}
