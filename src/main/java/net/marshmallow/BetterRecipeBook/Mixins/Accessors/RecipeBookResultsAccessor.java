package net.marshmallow.BetterRecipeBook.Mixins.Accessors;

import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RecipeBookResults.class)
public interface RecipeBookResultsAccessor {
    @Accessor()
    List<AnimatedResultButton> getResultButtons();
}
