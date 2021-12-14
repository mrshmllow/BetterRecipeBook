package marsh.town.brb.BrewingStand.forge;

import marsh.town.brb.forge.Mixins.Accessors.ForgePotionBrewingMixAccessor;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

public class AnimatedResultButtonImpl {
    public static Ingredient getIngredient(PotionBrewing.Mix<?> recipe) {
        return ((ForgePotionBrewingMixAccessor<?>) recipe).getIngredient();
    }
}
