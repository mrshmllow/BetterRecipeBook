package marsh.town.brb.smithingtable;

import marsh.town.brb.generic.GenericRecipeButton;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.SmithingMenu;

import java.util.function.Supplier;

public class SmithableRecipeButton extends GenericRecipeButton<SmithingRecipeCollection, BRBSmithingRecipe, SmithingMenu> {
    public SmithableRecipeButton(RegistryAccess registryAccess, Supplier<Boolean> filteringSupplier) {
        super(registryAccess, filteringSupplier);
    }
}
