package marsh.town.brb.brewingstand.forge;

import marsh.town.brb.forge.Mixins.Accessors.ForgePotionBrewingAccessor;
import marsh.town.brb.forge.Mixins.Accessors.ForgePotionBrewingMixAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class PlatformPotionUtilImpl {
    public static Ingredient getIngredient(PotionBrewing.Mix<?> recipe) {
        return ((ForgePotionBrewingMixAccessor<?>) recipe).getIngredient();
    }

    public static Potion getTo(PotionBrewing.Mix<Potion> recipe) {
        return (Potion) ((ForgePotionBrewingMixAccessor<?>) recipe).getTo().get();
    }

    public static Potion getFrom(PotionBrewing.Mix<?> recipe) {
        return (Potion) ((ForgePotionBrewingMixAccessor<?>) recipe).getFrom().get();
    }

    public static List<PotionBrewing.Mix<Potion>> getPotionMixes(ClientLevel level) {
        PotionBrewing brewing = level.potionBrewing();
        return ((ForgePotionBrewingAccessor) brewing).getPotionMixes();
    }
}
