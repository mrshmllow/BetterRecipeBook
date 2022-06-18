package marsh.town.brb.forge.Mixins.Accessors;

import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionBrewing.Mix.class)
public interface ForgePotionBrewingMixAccessor<T> {
    @Accessor("f_43532_")
    Holder.Reference<T> getFrom();
    @Accessor("f_43534_")
    Holder.Reference<T> getTo();
    @Accessor("ingredient")
    Ingredient getIngredient();
}
