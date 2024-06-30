package marsh.town.brb.brewingstand.neoforge;

import marsh.town.brb.neoforge.mixins.accessors.NeoForgePotionBrewingAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class PlatformPotionUtilImpl {
    public static Ingredient getIngredient(PotionBrewing.Mix<?> recipe) {
        return recipe.ingredient();
    }

    public static Potion getTo(PotionBrewing.Mix<Potion> recipe) {
        return recipe.to().value();
    }

    public static Potion getFrom(PotionBrewing.Mix<Potion> recipe) {
        return recipe.from().value();
    }

    public static List<PotionBrewing.Mix<Potion>> getPotionMixes(ClientLevel level) {
        PotionBrewing brewing = level.potionBrewing();
        return ((NeoForgePotionBrewingAccessor) brewing).getPotionMixes();
    }
}
