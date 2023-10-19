package marsh.town.brb.brewingstand.fabric;

import marsh.town.brb.fabric.Mixins.Accessors.FabricPotionBrewingAccessor;
import marsh.town.brb.fabric.Mixins.Accessors.FabricPotionBrewingMixAccessor;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class PlatformPotionUtilImpl {
    public static Ingredient getIngredient(PotionBrewing.Mix<?> recipe) {
        return ((FabricPotionBrewingMixAccessor<?>) recipe).getIngredient();
    }

    public static Potion getTo(PotionBrewing.Mix<?> recipe) {
        return (Potion) ((FabricPotionBrewingMixAccessor<?>) recipe).getTo();
    }

    public static Potion getFrom(PotionBrewing.Mix<?> recipe) {
        return (Potion) ((FabricPotionBrewingMixAccessor<?>) recipe).getFrom();
    }

    public static List<PotionBrewing.Mix<Potion>> getPotionMixes() {
        return FabricPotionBrewingAccessor.getPotionMixes();
    }
}
