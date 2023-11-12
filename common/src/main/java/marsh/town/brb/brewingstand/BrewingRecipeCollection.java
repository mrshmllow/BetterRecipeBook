package marsh.town.brb.brewingstand;

import com.google.common.collect.Lists;
import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.generic.GenericRecipeBookCollection;
import marsh.town.brb.generic.pins.Pinnable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class BrewingRecipeCollection extends GenericRecipeBookCollection<BrewableResult, BrewingStandMenu> implements Pinnable {
    private final BRBBookCategories.Category category;

    public BrewingRecipeCollection(List<BrewableResult> list, BrewingStandMenu menu, RegistryAccess registryAccess, BRBBookCategories.Category category) {
        super(list, menu, registryAccess);

        this.category = category;
    }

    public List<BrewableResult> getDisplayRecipes(boolean craftable) {
        List<BrewableResult> list = Lists.newArrayList();

        for (BrewableResult recipe : this.recipes) {
            if (recipe.hasMaterials(this.category, this.menu.slots) == craftable) {
                list.add(recipe);
            }
        }

        return list;
    }

    @Override
    public boolean has(ResourceLocation resourceLocation) {
        for (BrewableResult recipe : this.recipes) {
            if (recipe.id().equals(resourceLocation)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean atleastOneCraftable(NonNullList<Slot> slots) {
        for (BrewableResult recipe : this.recipes) {
            if (recipe.hasMaterials(this.category, slots)) {
                return true;
            }
        }

        return false;
    }
}
