package marsh.town.brb.mixins.accessors;

import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(OverlayRecipeComponent.class)
public interface OverlayRecipeComponentAccessor {

    @Accessor("recipeButtons")
    List<OverlayRecipeComponent.OverlayRecipeButton> getRecipeButtons();

    @Accessor("time")
    float getTime();

    @Accessor("isFurnaceMenu")
    boolean isFurnaceMenu();

}
