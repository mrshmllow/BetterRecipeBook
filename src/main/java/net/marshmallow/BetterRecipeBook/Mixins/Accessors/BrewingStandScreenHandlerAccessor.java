package net.marshmallow.BetterRecipeBook.Mixins.Accessors;

import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandScreenHandler.class)
public interface BrewingStandScreenHandlerAccessor {
    @Accessor("ingredientSlot")
    Slot getIngredientSlot();
}
