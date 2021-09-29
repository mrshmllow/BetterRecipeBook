package net.marshmallow.BetterRecipeBook.Mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(RecipeBook.class)
public class DisableBounce {
    @Final @Shadow
    protected Set<Identifier> toBeDisplayed;

    /**
     * @author marshmallow
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public boolean shouldDisplay(Recipe<?> recipe) {
        if (!BetterRecipeBook.config.enableBounce) {
            return false;
        } else {
            return toBeDisplayed.contains(recipe.getId());
        }
    }
}
