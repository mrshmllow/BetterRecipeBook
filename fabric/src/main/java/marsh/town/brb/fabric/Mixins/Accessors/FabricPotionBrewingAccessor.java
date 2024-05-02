package marsh.town.brb.fabric.Mixins.Accessors;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PotionBrewing.class)
public interface FabricPotionBrewingAccessor {
    @Accessor("potionMixes")
    List<PotionBrewing.Mix<Potion>> getPotionMixes();
}
