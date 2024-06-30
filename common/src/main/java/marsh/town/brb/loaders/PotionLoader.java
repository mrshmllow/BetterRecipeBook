package marsh.town.brb.loaders;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.brewingstand.BrewableResult;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

import java.util.ArrayList;
import java.util.List;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getPotionMixes;

public class PotionLoader {
    public static List<BrewableResult> POTIONS = new ArrayList<>();

    public static void init() {
        // Architectury calls CLIENT_LEVEL_LOAD before Minecraft#level is set. (as of version 12.0.27)
        // This means when PlatformPotionUtilImpl tries to access Minecraft#level it is null.
        // for this reason we need to forward the ClientLevel to the method
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(PotionLoader::load);
        LifecycleEvent.SERVER_LEVEL_UNLOAD.register((clientLevel) -> PotionLoader.clear());
    }

    private static void load(ClientLevel level) {
        PotionLoader.clearNoLog();

        List<PotionBrewing.Mix<Potion>> MIXES = getPotionMixes(level);

        for (PotionBrewing.Mix<Potion> potionRecipe : MIXES) {
            POTIONS.add(new BrewableResult(potionRecipe));
        }

        BetterRecipeBook.LOGGER.info("Loaded %d potions.".formatted(POTIONS.size()));
    }

    public static void clear() {
        BetterRecipeBook.LOGGER.info("Clearing potions...");
        clearNoLog();
    }

    private static void clearNoLog() {
        POTIONS.clear();
    }
}
