package marsh.town.brb.generic;

import marsh.town.brb.generic.pins.Pinnable;
import net.minecraft.resources.ResourceLocation;

public interface GenericRecipe extends Pinnable {
    ResourceLocation id();

    @Override
    default boolean has(ResourceLocation identifier) {
        return (id().equals(identifier));
    }
}
