package marsh.town.brb.BrewingStand;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class PlatformPotionUtil {
    @ExpectPlatform
    public static Ingredient getIngredient(PotionBrewing.Mix<?> recipe) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Potion getTo(PotionBrewing.Mix<?> recipe) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Potion getFrom(PotionBrewing.Mix<?> recipe) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<PotionBrewing.Mix<Potion>> getPotionMixes() {
        throw new AssertionError();
    }
}
