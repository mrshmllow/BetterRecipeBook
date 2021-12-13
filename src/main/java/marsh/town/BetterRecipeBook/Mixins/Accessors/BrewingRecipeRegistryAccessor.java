package marsh.town.BetterRecipeBook.Mixins.Accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

@Mixin(PotionBrewing.class)
public interface BrewingRecipeRegistryAccessor {
    @Accessor("POTION_MIXES")
    static List<PotionBrewing.Mix<Potion>> getPotionMixes() {
        throw new AssertionError();
    }
}
