package marsh.town.brb.util;

import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;

public class RecipeMenuUtil {

    public static boolean isRecipeSlot(RecipeBookMenu<?> menu, int slot) {
        if (menu instanceof AbstractFurnaceMenu) {
            return AbstractFurnaceMenu.INGREDIENT_SLOT == slot;
        } else {
            return isCraftingGridSlot(menu, slot);
        }
    }

    public static boolean isCraftingGridSlot(RecipeBookMenu<?> menu, int slot) {
        return slot > menu.getResultSlotIndex() && slot < menu.getSize();
    }

    public static boolean isResultSlot(RecipeBookMenu<?> menu, int slot) {
        return menu.getResultSlotIndex() == slot;
    }

    public static boolean isCraftingMenuSlot(RecipeBookMenu<?> menu, int slot) {
        return isCraftingGridSlot(menu, slot) || isResultSlot(menu, slot);
    }

}
