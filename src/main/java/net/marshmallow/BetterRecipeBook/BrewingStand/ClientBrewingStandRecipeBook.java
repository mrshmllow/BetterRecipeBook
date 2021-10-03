package net.marshmallow.BetterRecipeBook.BrewingStand;

import net.marshmallow.BetterRecipeBook.Mixins.Accessors.BrewingRecipeRegistryAccessor;
import net.marshmallow.BetterRecipeBook.Mixins.Accessors.BrewingRecipeRegistryRecipeAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.book.RecipeBookCategory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClientBrewingStandRecipeBook extends RecipeBook {
    private boolean filteringCraftable;

    public boolean isFilteringCraftable() {
        return filteringCraftable;
    }

    public List<BrewingResult> getResultsForCategory(BrewingRecipeBookGroup group) {
        List<BrewingRecipeRegistry.Recipe<Potion>> recipeCollection = new ArrayList<>(BrewingRecipeRegistryAccessor.getPotionRecipes());

        // Remove duplicates, or so they say
        Set<BrewingRecipeRegistry.Recipe<Potion>> set = new LinkedHashSet<>(recipeCollection);
        recipeCollection.clear();
        recipeCollection.addAll(set);

        List<BrewingResult> brewingResults = new ArrayList<>();

        for (BrewingRecipeRegistry.Recipe<Potion> potionRecipe : recipeCollection) {
            if (group == BrewingRecipeBookGroup.BREWING_POTION) {
                brewingResults.add(new BrewingResult(PotionUtil.setPotion(new ItemStack(Items.POTION), (Potion) ((BrewingRecipeRegistryRecipeAccessor)potionRecipe).getOutput()), potionRecipe));
            } else if (group == BrewingRecipeBookGroup.BREWING_SPLASH_POTION) {
                brewingResults.add(new BrewingResult(PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), (Potion) ((BrewingRecipeRegistryRecipeAccessor)potionRecipe).getOutput()), potionRecipe));
            } else if (group == BrewingRecipeBookGroup.BREWING_LINGERING_POTION) {
                brewingResults.add(new BrewingResult(PotionUtil.setPotion(new ItemStack(Items.LINGERING_POTION), (Potion) ((BrewingRecipeRegistryRecipeAccessor)potionRecipe).getOutput()), potionRecipe));
            }
        }

        return brewingResults;
    }

    public boolean isFilteringCraftable(RecipeBookCategory category) {
        return filteringCraftable;
    }

    public void setFilteringCraftable(boolean filteringCraftable) {
        this.filteringCraftable = filteringCraftable;
    }
}
