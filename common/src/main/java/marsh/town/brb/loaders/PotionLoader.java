package marsh.town.brb.loaders;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.brewingstand.BrewableResult;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

import java.util.ArrayList;
import java.util.List;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getPotionMixes;

public class PotionLoader {
    public static List<BrewableResult> POTIONS = new ArrayList<>();

    public static void init() {
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register((clientLevel) -> PotionLoader.load());
        LifecycleEvent.SERVER_LEVEL_UNLOAD.register((clientLevel) -> PotionLoader.clear());
    }

    private static void load() {
        PotionLoader.clear();

        BetterRecipeBook.LOGGER.info("Loading Potions...");

        List<PotionBrewing.Mix<Potion>> MIXES = getPotionMixes();

        for (PotionBrewing.Mix<Potion> potionRecipe : MIXES) {
            POTIONS.add(new BrewableResult(potionRecipe));
        }
    }

    public static void clear() {
        BetterRecipeBook.LOGGER.info("Clearing potions...");
        POTIONS.clear();
    }
}
