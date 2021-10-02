package net.marshmallow.BetterRecipeBook.Mixins.Accessors;

import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingRecipeRegistry.Recipe.class)
public interface BrewingRecipeRegistryRecipeAccessor<T> {
    @Accessor("input")
    T getInput();
    @Accessor("output")
    T getOutput();
    @Accessor("ingredient")
    Ingredient getIngredient();
}
