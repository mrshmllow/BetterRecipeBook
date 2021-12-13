package marsh.town.brb.Mixins.Pins;

import marsh.town.brb.Mixins.Accessors.TexturedButtonWidgetAccessor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(ContainerEventHandler.class)
public interface RemoveButtonFocus {
    @Shadow List<? extends GuiEventListener> children();
    @Shadow void setFocused(@Nullable GuiEventListener focused);
    @Shadow void setDragging(boolean dragging);

    /**
     * @author marshmallow
     * I have sinned.
     */
    @Overwrite
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        Iterator<? extends GuiEventListener> var6 = this.children().iterator();

        GuiEventListener element;
        do {
            if (!var6.hasNext()) {
                return false;
            }

            element = var6.next();
        } while(!element.mouseClicked(mouseX, mouseY, button));

        if (element instanceof ImageButton) {
            if (((TexturedButtonWidgetAccessor) element).getResourceLocation().equals(new ResourceLocation("textures/gui/recipe_button.png"))) {
                return true;
            }
        }

        this.setFocused(element);

        if (button == 0) {
            this.setDragging(true);
        }

        return true;
    }
}
