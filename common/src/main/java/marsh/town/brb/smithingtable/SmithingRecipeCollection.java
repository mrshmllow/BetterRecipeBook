package marsh.town.brb.smithingtable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import marsh.town.brb.smithingtable.recipe.BRBSmithingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;

import java.util.List;

public class SmithingRecipeCollection {
    private final List<BRBSmithingRecipe> recipes;
    private SmithingMenu smithingScreenHandler;
    private RegistryAccess registryAccess;

    public SmithingRecipeCollection(List<? extends BRBSmithingRecipe> list, SmithingMenu smithingScreenHandler, RegistryAccess registryAccess) {
        this.recipes = ImmutableList.copyOf(list);
        this.smithingScreenHandler = smithingScreenHandler;
        this.registryAccess = registryAccess;
    }

    public BRBSmithingRecipe getFirst() {
        return this.recipes.get(0);
    }

    public List<BRBSmithingRecipe> getDisplayRecipes(boolean craftable) {
        List<BRBSmithingRecipe> list = Lists.newArrayList();

        for (BRBSmithingRecipe recipe : this.recipes) {
            if (recipe.hasMaterials(smithingScreenHandler.slots, registryAccess) == craftable) {
                list.add(recipe);
            }
        }

        return list;
    }

    public boolean atleastOneCraftable(NonNullList<Slot> slots) {
        for (BRBSmithingRecipe recipe : this.recipes) {
            if (recipe.hasMaterials(slots, registryAccess)) {
                return true;
            }
        }

        return false;
    }
}
