package marsh.town.brb.generic;

import com.google.common.collect.ImmutableList;
import marsh.town.brb.generic.pins.Pinnable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public abstract class GenericRecipeBookCollection<R extends GenericRecipe, M extends AbstractContainerMenu> implements Pinnable {
    protected final RegistryAccess registryAccess;
    protected List<R> recipes;
    protected M menu;

    protected GenericRecipeBookCollection(List<? extends R> list, M menu, RegistryAccess registryAccess) {
        this.menu = menu;
        this.recipes = ImmutableList.copyOf(list);
        this.registryAccess = registryAccess;
    }

    public List<R> getRecipes() {
        return recipes;
    }

    protected abstract List<R> getDisplayRecipes(boolean craftable);

    public boolean has(ResourceLocation resourceLocation) {
        for (R recipe : getRecipes()) {
            if (recipe.id().equals(resourceLocation)) {
                return true;
            }
        }

        return false;
    }

    public R getFirst() {
        return this.getRecipes().get(0);
    }

    protected abstract boolean atleastOneCraftable(NonNullList<Slot> slots);
}
