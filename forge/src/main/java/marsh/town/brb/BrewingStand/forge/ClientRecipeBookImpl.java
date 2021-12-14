package marsh.town.brb.BrewingStand.forge;

import marsh.town.brb.forge.Mixins.Accessors.ForgePotionBrewingAccessor;
import marsh.town.brb.forge.Mixins.Accessors.ForgePotionBrewingMixAccessor;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

import java.util.List;

public class ClientRecipeBookImpl {
    public static List<PotionBrewing.Mix<Potion>> getPotionMixes() {
        return ForgePotionBrewingAccessor.getPotionMixes();
    }

    public static Potion getTo(PotionBrewing.Mix<?> recipe) {
        return (Potion) ((ForgePotionBrewingMixAccessor<?>) recipe).getTo().get();
    }
}
