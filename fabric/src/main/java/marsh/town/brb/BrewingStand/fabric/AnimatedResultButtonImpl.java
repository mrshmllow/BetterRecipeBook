package marsh.town.brb.BrewingStand.fabric;

import marsh.town.brb.fabric.Mixins.Accessors.FabricPotionBrewingMixAccessor;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

public class AnimatedResultButtonImpl {
    public static Ingredient getIngredient(PotionBrewing.Mix<?> recipe) {
        return ((FabricPotionBrewingMixAccessor<?>) recipe).getIngredient();
    }
}
