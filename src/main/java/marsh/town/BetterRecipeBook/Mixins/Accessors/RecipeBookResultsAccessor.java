package marsh.town.BetterRecipeBook.Mixins.Accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;

@Mixin(RecipeBookPage.class)
public interface RecipeBookResultsAccessor {
    @Accessor()
    List<RecipeButton> getButtons();
    @Accessor()
    OverlayRecipeComponent getOverlay();
}
