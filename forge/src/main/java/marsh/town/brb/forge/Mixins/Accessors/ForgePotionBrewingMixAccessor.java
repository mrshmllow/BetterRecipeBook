package marsh.town.brb.forge.Mixins.Accessors;

import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionBrewing.Mix.class)
public interface ForgePotionBrewingMixAccessor<T extends ForgeRegistryEntry<T>> {
    @Accessor("from")
    IRegistryDelegate<T> getFrom();
    @Accessor("to")
    IRegistryDelegate<T> getTo();
    @Accessor("ingredient")
    Ingredient getIngredient();
}
