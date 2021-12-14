package marsh.town.brb.fabric;

import marsh.town.brb.fabric.Mixins.Accessors.FabricPotionBrewingMixAccessor;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

public class PinnedRecipeManagerImpl {
    public static Potion getTo(PotionBrewing.Mix<?> recipe) {
        return (Potion) ((FabricPotionBrewingMixAccessor<?>) recipe).getTo();
    }
}
