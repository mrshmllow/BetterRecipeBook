package marsh.town.brb.smithingtable;

import com.google.common.collect.Lists;
import marsh.town.brb.generic.GenericRecipeBookCollection;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;

import java.util.List;

public class SmithingRecipeCollection extends GenericRecipeBookCollection<BRBSmithingRecipe, SmithingMenu> {
    public SmithingRecipeCollection(List<? extends BRBSmithingRecipe> list, SmithingMenu menu, RegistryAccess registryAccess) {
        super(list, menu, registryAccess);
    }

    public List<BRBSmithingRecipe> getDisplayRecipes(boolean craftable) {
        List<BRBSmithingRecipe> list = Lists.newArrayList();

        for (BRBSmithingRecipe recipe : this.recipes) {
            if (recipe.hasMaterials(this.menu.slots, registryAccess) == craftable) {
                list.add(recipe);
            }
        }

        return list;
    }

    @Override
    public boolean atleastOneCraftable(NonNullList<Slot> slots) {
        for (BRBSmithingRecipe recipe : this.recipes) {
            if (recipe.hasMaterials(slots, registryAccess)) {
                return true;
            }
        }

        return false;
    }
}
