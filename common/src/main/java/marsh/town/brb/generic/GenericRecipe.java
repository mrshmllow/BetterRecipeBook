package marsh.town.brb.generic;

import marsh.town.brb.generic.pins.Pinnable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface GenericRecipe extends Pinnable {
    ResourceLocation id();

    @Override
    default boolean has(ResourceLocation identifier) {
        return (id().equals(identifier));
    }

    ItemStack getResult(RegistryAccess registryAccess);

    String getSearchString();
}
