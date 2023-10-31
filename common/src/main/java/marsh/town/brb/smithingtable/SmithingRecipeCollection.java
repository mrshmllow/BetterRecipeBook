package marsh.town.brb.smithingtable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;

import java.util.List;

public class SmithingRecipeCollection {
    private final RegistryAccess registryAccess;
    private final List<SmithableResult> recipes;
    private SmithingMenu smithingScreenHandler;

    public SmithingRecipeCollection(RegistryAccess registryAccess, List<SmithableResult> list, SmithingMenu smithingScreenHandler) {
        this.registryAccess = registryAccess;
        this.recipes = ImmutableList.copyOf(list);
        this.smithingScreenHandler = smithingScreenHandler;
    }

    public SmithableResult getFirst() {
        return this.recipes.get(0);
    }

    public List<SmithableResult> getDisplayRecipes(boolean craftable) {
        List<SmithableResult> list = Lists.newArrayList();

        for (SmithableResult recipe : this.recipes) {
            if (recipe.hasMaterials(smithingScreenHandler.slots) == craftable) {
                list.add(recipe);
            }
        }

        return list;
    }

    public boolean atleastOneCraftable(NonNullList<Slot> slots) {
        for (SmithableResult recipe : this.recipes) {
            if (recipe.hasMaterials(slots)) {
                return true;
            }
        }

        return false;
    }
}
