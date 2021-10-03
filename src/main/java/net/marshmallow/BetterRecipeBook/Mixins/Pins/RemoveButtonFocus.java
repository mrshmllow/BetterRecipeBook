package net.marshmallow.BetterRecipeBook.Mixins.Pins;

import net.marshmallow.BetterRecipeBook.Mixins.Accessors.TexturedButtonWidgetAccessor;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(ParentElement.class)
public interface RemoveButtonFocus {
    @Shadow List<? extends Element> children();
    @Shadow void setFocused(@Nullable Element focused);
    @Shadow void setDragging(boolean dragging);

    /**
     * @author marshmallow
     * I have sinned.
     */
    @Overwrite
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        Iterator<? extends Element> var6 = this.children().iterator();

        Element element;
        do {
            if (!var6.hasNext()) {
                return false;
            }

            element = var6.next();
        } while(!element.mouseClicked(mouseX, mouseY, button));

        if (element instanceof TexturedButtonWidget) {
            if (((TexturedButtonWidgetAccessor) element).getTexture().equals(new Identifier("textures/gui/recipe_button.png"))) {
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
