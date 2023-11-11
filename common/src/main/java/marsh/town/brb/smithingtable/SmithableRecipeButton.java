package marsh.town.brb.smithingtable;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.GenericRecipeButton;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.SmithingMenu;

public class SmithableRecipeButton extends GenericRecipeButton<SmithingRecipeCollection, BRBSmithingRecipe, SmithingMenu> {
    public SmithableRecipeButton(RegistryAccess registryAccess) {
        super(registryAccess);
    }

    @Override
    protected boolean selfRecallFiltering() {
        return BetterRecipeBook.rememberedSmithableToggle;
    }
}
