package marsh.town.brb.Mixins.Accessors;

import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(OverlayRecipeComponent.class)
public interface OverlayRecipeComponentAccessor {
    @Accessor()
    List<OverlayRecipeComponent.OverlayRecipeButton> getRecipeButtons();
}
