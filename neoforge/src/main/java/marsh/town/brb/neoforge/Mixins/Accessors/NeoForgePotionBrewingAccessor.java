package marsh.town.brb.neoforge.mixins.accessors;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PotionBrewing.class)
public interface NeoForgePotionBrewingAccessor {
    @Accessor("potionMixes")
    List<PotionBrewing.Mix<Potion>> getPotionMixes();
}
