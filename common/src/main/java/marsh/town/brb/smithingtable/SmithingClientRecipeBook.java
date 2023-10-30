package marsh.town.brb.smithingtable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;

public class SmithingClientRecipeBook extends RecipeBook {
    private boolean filteringCraftable;

    public boolean isFilteringCraftable() {
        return filteringCraftable;
    }

    public List<SmithingRecipeCollection> getCollectionsForCategory(SmithingRecipeBookGroup group, SmithingMenu smithingScreenHandler) {
        List<RecipeHolder<SmithingRecipe>> recipes = Minecraft.getInstance().getConnection().getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
        List<SmithingRecipeCollection> results = new ArrayList<>();
        RegistryAccess registryAccess = Minecraft.getInstance().getConnection().registryAccess();

        for (RecipeHolder<SmithingRecipe> recipe : recipes) {
            SmithingRecipe value = recipe.value();

            if (group == SmithingRecipeBookGroup.SMITHING_SEARCH) {
                if (value instanceof SmithingTransformRecipe) {
                    results.add(new SmithingRecipeCollection(registryAccess, List.of(SmithableResult.of((SmithingTransformRecipe) value)), smithingScreenHandler));
                } else if (value instanceof SmithingTrimRecipe) {
                    results.add(new SmithingRecipeCollection(registryAccess, SmithableResult.of((SmithingTrimRecipe) value), smithingScreenHandler));
                }
            } else if (group == SmithingRecipeBookGroup.SMITHING_TRANSFORM) {
                if (value instanceof SmithingTransformRecipe) {
                    results.add(new SmithingRecipeCollection(registryAccess, List.of(SmithableResult.of((SmithingTransformRecipe) value)), smithingScreenHandler));
                }
            } else if (value instanceof SmithingTrimRecipe) {
                results.add(new SmithingRecipeCollection(registryAccess, SmithableResult.of((SmithingTrimRecipe) value), smithingScreenHandler));
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
