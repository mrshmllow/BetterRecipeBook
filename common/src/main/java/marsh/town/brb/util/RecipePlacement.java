package marsh.town.brb.util;

import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipePlacement implements PlaceRecipe<Ingredient> {

    protected List<List<Ingredient>> placement = new ArrayList<>();

    public RecipePlacement(int size) {
        for (int i = 0; i < size; i++) placement.add(new ArrayList<>());
    }

    @Override
    public void addItemToSlot(Iterator<Ingredient> iterator, int menuSlot, int j, int k, int l) {
        Ingredient ingredient = iterator.next();
        if (!ingredient.isEmpty() && menuSlot < placement.size()) {
            placement.get(menuSlot).add(ingredient);
        }
    }

    public List<List<Ingredient>> getPlacement() {
        return placement;
    }

    public static List<List<Ingredient>> create(Recipe<?> recipe, int gridWidth, int gridHeight) {
        RecipePlacement placer = new RecipePlacement(gridWidth * gridHeight);
        placer.placeRecipe(gridWidth, gridHeight, -1, recipe, recipe.getIngredients().iterator(), 0);
        return placer.getPlacement();
    }

}
