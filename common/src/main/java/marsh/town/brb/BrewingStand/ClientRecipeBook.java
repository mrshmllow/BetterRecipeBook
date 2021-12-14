package marsh.town.brb.BrewingStand;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClientRecipeBook extends RecipeBook {
    private boolean filteringCraftable;

    public boolean isFilteringCraftable() {
        return filteringCraftable;
    }

    @ExpectPlatform
    public static List<PotionBrewing.Mix<Potion>> getPotionMixes() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Potion getTo(PotionBrewing.Mix<?> recipe) {
        throw new AssertionError();
    }

    public static List<Result> getResultsForCategory(RecipeBookGroup group) {
        List<PotionBrewing.Mix<Potion>> recipeCollection = new ArrayList<>(getPotionMixes());

        // Remove duplicates, or so they say
        Set<PotionBrewing.Mix<Potion>> set = new LinkedHashSet<>(recipeCollection);
        recipeCollection.clear();
        recipeCollection.addAll(set);

        List<Result> results = new ArrayList<>();

        for (PotionBrewing.Mix<Potion> potionRecipe : recipeCollection) {
            if (group == RecipeBookGroup.BREWING_POTION) {
                results.add(new Result(PotionUtils.setPotion(new ItemStack(Items.POTION), getTo(potionRecipe)), potionRecipe));
            } else if (group == RecipeBookGroup.BREWING_SPLASH_POTION) {
                results.add(new Result(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), getTo(potionRecipe)), potionRecipe));
            } else if (group == RecipeBookGroup.BREWING_LINGERING_POTION) {
                results.add(new Result(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), getTo(potionRecipe)), potionRecipe));
            }
        }

        return results;
    }

    public boolean isFiltering(RecipeBookType category) {
        return filteringCraftable;
    }

    public void setFilteringCraftable(boolean filteringCraftable) {
        this.filteringCraftable = filteringCraftable;
    }
}
