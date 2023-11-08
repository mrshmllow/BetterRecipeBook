package marsh.town.brb.generic;

import java.util.List;

public interface GenericRecipeBookCollection {
    List<? extends GenericRecipe> getRecipes();
}
