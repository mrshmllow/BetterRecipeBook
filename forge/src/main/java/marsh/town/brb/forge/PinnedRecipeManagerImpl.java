package marsh.town.brb.forge;

import marsh.town.brb.forge.Mixins.Accessors.ForgePotionBrewingMixAccessor;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

public class PinnedRecipeManagerImpl {
    public static Potion getTo(PotionBrewing.Mix<?> recipe) {
        return (Potion) ((ForgePotionBrewingMixAccessor<?>) recipe).getTo().get();
    }
}
