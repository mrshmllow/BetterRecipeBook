package net.marshmallow.BetterRecipeBook.Mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(RecipeBook.class)
public class DisableBounce {
    @Final @Shadow
    protected Set<ResourceLocation> highlight;

    /**
     * @author marshmallow
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public boolean willHighlight(Recipe<?> recipe) {
        if (!BetterRecipeBook.config.newRecipes.enableBounce) {
            return false;
        } else {
            return highlight.contains(recipe.getId());
        }
    }
}
