package marsh.town.brb.brewingstand;

import marsh.town.brb.generic.GenericGhostRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class TestBrewingGhostRecipe extends GenericGhostRecipe<BrewableResult> {
    public TestBrewingGhostRecipe(Consumer<ItemStack> onGhostUpdate, RegistryAccess registryAccess) {
        super(onGhostUpdate, registryAccess);
    }
}
