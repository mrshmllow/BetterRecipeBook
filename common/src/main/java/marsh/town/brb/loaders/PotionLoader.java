package marsh.town.brb.loaders;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.brewingstand.BrewableResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.ArrayList;
import java.util.List;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getPotionMixes;
import static marsh.town.brb.brewingstand.PlatformPotionUtil.getTo;

public class PotionLoader {
    public static List<BrewableResult> POTIONS = new ArrayList<>();
    public static List<BrewableResult> SPLASHES = new ArrayList<>();
    public static List<BrewableResult> LINGERINGS = new ArrayList<>();

    public static void init() {
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register((clientLevel) -> PotionLoader.load());
        LifecycleEvent.SERVER_LEVEL_UNLOAD.register((clientLevel) -> PotionLoader.clear());
    }

    private static void load() {
        PotionLoader.clear();

        BetterRecipeBook.LOGGER.info("Loading Potions...");

        List<PotionBrewing.Mix<Potion>> MIXES = getPotionMixes();

        for (PotionBrewing.Mix<Potion> potionRecipe : MIXES) {
            POTIONS.add(new BrewableResult(PotionUtils.setPotion(new ItemStack(Items.POTION), getTo(potionRecipe)), potionRecipe));
            SPLASHES.add(new BrewableResult(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), getTo(potionRecipe)), potionRecipe));
            LINGERINGS.add(new BrewableResult(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), getTo(potionRecipe)), potionRecipe));
        }
    }

    public static void clear() {
        BetterRecipeBook.LOGGER.info("Clearing potions...");
        POTIONS.clear();
        SPLASHES.clear();
        LINGERINGS.clear();
    }
}
