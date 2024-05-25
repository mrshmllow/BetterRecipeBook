package marsh.town.brb.neoforge.mixins.accessors;

import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionBrewing.Mix.class)
public interface NeoForgePotionBrewingMixAccessor<T> {
    @Accessor("from")
    Holder<T> getFrom();
    @Accessor("to")
    Holder<T> getTo();
    @Accessor("ingredient")
    Ingredient getIngredient();
}
