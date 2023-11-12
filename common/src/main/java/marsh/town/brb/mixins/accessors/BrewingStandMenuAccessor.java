package marsh.town.brb.mixins.accessors;

import net.minecraft.world.inventory.BrewingStandMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandMenu.class)
public interface BrewingStandMenuAccessor {
    @Accessor("INGREDIENT_SLOT")
    static int getINGREDIENT_SLOT() {
        throw new AssertionError();
    }

    @Accessor("BOTTLE_SLOT_START")
    static int getBOTTLE_SLOT_START() {
        throw new AssertionError();
    }

    @Accessor("BOTTLE_SLOT_END")
    static int getBOTTLE_SLOT_END() {
        throw new AssertionError();
    }
}
