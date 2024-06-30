package marsh.town.brb.brewingstand;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.multiplayer.ClientLevel;
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
    public static Potion getTo(PotionBrewing.Mix<Potion> recipe) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Potion getFrom(PotionBrewing.Mix<Potion> recipe) {
        throw new AssertionError();
    }

    //TODO still needs to be platform dependant?
    @ExpectPlatform
    public static List<PotionBrewing.Mix<Potion>> getPotionMixes(ClientLevel level) {
        throw new AssertionError();
    }
}
