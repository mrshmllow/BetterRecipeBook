package net.marshmallow.BetterRecipeBook.Mixins.Accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;

@Mixin(OverlayRecipeComponent.class)
public interface RecipeAlternativesWidgetAccessor {
    @Accessor()
    List<OverlayRecipeComponent.OverlayRecipeButton> getRecipeButtons();
}
