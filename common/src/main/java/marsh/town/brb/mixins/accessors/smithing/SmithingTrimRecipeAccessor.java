package marsh.town.brb.mixins.accessors.smithing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTrimRecipe.class)
public interface SmithingTrimRecipeAccessor {
    @Accessor("template")
    Ingredient getUnderlyingTemplate();

    @Accessor("base")
    Ingredient getUnderlyingBase();

    @Accessor("addition")
    Ingredient getUnderlyingAddition();

    @Accessor("id")
    ResourceLocation getId();
}
