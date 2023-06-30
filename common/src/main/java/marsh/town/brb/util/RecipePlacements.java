package marsh.town.brb.util;

import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipePlacements implements PlaceRecipe<Ingredient> {

    protected List<List<Ingredient>> placements = new ArrayList<>();

    public RecipePlacements(int size) {
        for (int i = 0; i < size; i++) placements.add(new ArrayList<>());
    }

    @Override
    public void addItemToSlot(Iterator<Ingredient> iterator, int menuSlot, int j, int k, int l) {
        Ingredient ingredient = iterator.next();
        if (!ingredient.isEmpty() && menuSlot < placements.size()) {
            placements.get(menuSlot).add(ingredient);
        }
    }

    public List<List<Ingredient>> getPlacements() {
        return placements;
    }

    public static List<List<Ingredient>> getPlacements(Recipe<?> recipe, int gridWidth, int gridHeight) {
        RecipePlacements placer = new RecipePlacements(gridWidth * gridHeight);
        placer.placeRecipe(gridWidth, gridHeight, 999, recipe, recipe.getIngredients().iterator(), 0);
        return placer.getPlacements();
    }

}
