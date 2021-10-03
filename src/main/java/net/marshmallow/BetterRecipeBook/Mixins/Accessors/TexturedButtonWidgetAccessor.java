package net.marshmallow.BetterRecipeBook.Mixins.Accessors;

import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TexturedButtonWidget.class)
public interface TexturedButtonWidgetAccessor {
    @Accessor()
    Identifier getTexture();
}
