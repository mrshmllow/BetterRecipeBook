package marsh.town.brb.Mixins.Accessors;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingMenu.class)
public interface CraftingMenuAccessor {

    @Accessor("craftSlots")
    CraftingContainer getCraftingContainer();

}
