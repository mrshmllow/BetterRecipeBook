package marsh.town.brb.Loaders;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.BrewingStand.Result;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.ArrayList;
import java.util.List;

import static marsh.town.brb.BrewingStand.PlatformPotionUtil.getPotionMixes;
import static marsh.town.brb.BrewingStand.PlatformPotionUtil.getTo;

public class PotionLoader {
    public static List<Result> POTIONS = new ArrayList<>();
    public static List<Result> SPLASHES = new ArrayList<>();
    public static List<Result> LINGERINGS = new ArrayList<>();

    public static void init() {
        BetterRecipeBook.LOGGER.info("Loading Potions...");

        List<PotionBrewing.Mix<Potion>> MIXES = getPotionMixes();

        for (PotionBrewing.Mix<Potion> potionRecipe : MIXES) {
            POTIONS.add(new Result(PotionUtils.setPotion(new ItemStack(Items.POTION), getTo(potionRecipe)), potionRecipe));
            SPLASHES.add(new Result(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), getTo(potionRecipe)), potionRecipe));
            LINGERINGS.add(new Result(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), getTo(potionRecipe)), potionRecipe));
        }
    }
}
