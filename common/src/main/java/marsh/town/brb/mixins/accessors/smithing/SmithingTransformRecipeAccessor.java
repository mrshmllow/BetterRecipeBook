package marsh.town.brb.mixins.accessors.smithing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class)
public interface SmithingTransformRecipeAccessor {
    @Accessor("template")
    Ingredient getUnderlyingTemplate();

    @Accessor("base")
    Ingredient getUnderlyingBase();

    @Accessor("addition")
    Ingredient getUnderlyingAddition();

    @Accessor("result")
    ItemStack getResult();
}
