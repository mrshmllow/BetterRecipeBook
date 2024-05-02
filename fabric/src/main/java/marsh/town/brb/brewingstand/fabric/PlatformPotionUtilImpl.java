package marsh.town.brb.brewingstand.fabric;

import marsh.town.brb.fabric.Mixins.Accessors.FabricPotionBrewingAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class PlatformPotionUtilImpl {
    public static Ingredient getIngredient(PotionBrewing.Mix<?> recipe) {
        return recipe.ingredient();
    }

    public static Potion getTo(PotionBrewing.Mix<?> recipe) {
        return (Potion) recipe.to().value();
    }

    public static Potion getFrom(PotionBrewing.Mix<?> recipe) {
        return (Potion) recipe.from().value();
    }

    public static List<PotionBrewing.Mix<Potion>> getPotionMixes() {
        var mc = Minecraft.getInstance();
        PotionBrewing brewing;
        if (mc.isLocalServer()) {
            brewing = mc.getSingleplayerServer().potionBrewing();
        } else {
            brewing = mc.getConnection().potionBrewing();
        }
        return ((FabricPotionBrewingAccessor) brewing).getPotionMixes();
    }
}
