package marsh.town.brb.BrewingStand.fabric;

import marsh.town.brb.fabric.Mixins.Accessors.FabricPotionBrewingAccessor;
import marsh.town.brb.fabric.Mixins.Accessors.FabricPotionBrewingMixAccessor;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

import java.util.List;

public class ClientRecipeBookImpl {
    public static List<PotionBrewing.Mix<Potion>> getPotionMixes() {
        return FabricPotionBrewingAccessor.getPotionMixes();
    }

    public static Potion getTo(PotionBrewing.Mix<?> recipe) {
        return (Potion) ((FabricPotionBrewingMixAccessor<?>) recipe).getTo();
    }
}
