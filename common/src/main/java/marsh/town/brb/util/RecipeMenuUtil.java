package marsh.town.brb.util;

import net.minecraft.world.inventory.RecipeBookMenu;

public class RecipeMenuUtil {

    public static boolean isCraftingGridSlot(RecipeBookMenu<?> menu, int slot) {
        return slot > menu.getResultSlotIndex() && slot < menu.getSize();
    }

    public static boolean isCraftingResultSlot(RecipeBookMenu<?> menu, int slot) {
        return menu.getResultSlotIndex() == slot;
    }

    public static boolean isCraftingMenuSlot(RecipeBookMenu<?> menu, int slot) {
        return isCraftingGridSlot(menu, slot) || isCraftingResultSlot(menu, slot);
    }

}
