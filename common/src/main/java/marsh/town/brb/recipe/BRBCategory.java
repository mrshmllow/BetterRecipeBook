package marsh.town.brb.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class BRBCategory {
    private final ResourceLocation resourceLocation;
    private final Item item;

    BRBCategory(ResourceLocation resourceLocation, Item item) {
        this.resourceLocation = resourceLocation;
        this.item = item;
    }
}
