package marsh.town.brb.BrewingStand.forge;

import marsh.town.brb.forge.Mixins.Accessors.ForgePotionBrewingMixAccessor;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

public class ResultImpl {
    public static Potion getFrom(PotionBrewing.Mix<?> recipe) {
        return (Potion) ((ForgePotionBrewingMixAccessor<?>) recipe).getFrom().get();
    }

    public static Ingredient getIngredient(PotionBrewing.Mix<?> recipe) {
        return ((ForgePotionBrewingMixAccessor<?>) recipe).getIngredient();
    }
}
