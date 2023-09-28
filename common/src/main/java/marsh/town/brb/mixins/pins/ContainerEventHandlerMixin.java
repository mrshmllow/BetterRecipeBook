package marsh.town.brb.mixins.pins;

import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ContainerEventHandler.class)
public interface ContainerEventHandlerMixin {
    @Shadow List<? extends GuiEventListener> children();
    @Shadow void setFocused(@Nullable GuiEventListener focused);
    @Shadow void setDragging(boolean dragging);

    //TODO Is this sin needed? Because If it is I have no idea.
    /**
     * @author marshmallow
     * I have sinned.
     *
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
            if (((ImageButtonAccessor) element).getSprites().enabled().equals(new ResourceLocation("textures/gui/recipe_button.png"))) {
                return true;
            }
        }

        this.setFocused(element);

        if (button == 0) {
            this.setDragging(true);
        }

        return true;
    }*/
}
