package marsh.town.brb.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Simple utility to handle client side movement of items
 * @author Tau
 */
public class ClientInventoryUtil {

    /**
     * Stores an item from a slot/cursor to a slot within the bounds of indexCheck<br>
     * Note: If there is an item being carried bny the cursor, and you aren't storing the cursor item, the cursor item will be stored/dropped
     * @param fromSlot the location of the item, -1 to store the item being carried by the cursor
     * @param indexCheck the bounds of the "storage" - if the predicate returns false, that slot will not be used as storage
     */
    public static void storeItem(int fromSlot, Predicate<Integer> indexCheck) {
        MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
        Minecraft minecraft = Minecraft.getInstance();
        AbstractContainerMenu menu = minecraft.player.containerMenu;
        if (menu == null) return;

        // if fromSlot is null assume the item is being carried
        if (fromSlot >= 0) {
            if (menu.slots.get(fromSlot).getItem().isEmpty()) return;

            // if there is already an item in the hand, drop it.
            if (!menu.getCarried().isEmpty()) {
                storeItem(-1, indexCheck);
            }

            gameMode.handleInventoryMouseClick(menu.containerId, fromSlot, 0, ClickType.PICKUP, minecraft.player);
        } else if (menu.getCarried().isEmpty()) {
            return;
        }

        // sort the slots so full slots will be checked first
        List<Slot> slots = new ArrayList<>(menu.slots);
        slots.sort((a, b) -> Boolean.compare(a.getItem().isEmpty(), b.getItem().isEmpty()));

        int count = menu.getCarried().getCount();
        for (Slot slot : slots) {
            if (count <= 0) break;
            if (indexCheck.test(slot.index) && (ItemStack.isSameItemSameComponents(menu.getCarried(), slot.getItem()) || slot.getItem().isEmpty())) {
                int slotCount = slot.getItem().getCount();
                if (slotCount < slot.getMaxStackSize()) {
                    count -= Math.max(0, slot.getMaxStackSize() - slotCount);
                    gameMode.handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, minecraft.player);
                }
            }
        }
        if (count > 0) {
            dropItem(-1, true, false);
        }
    }

    /**
     * Drops an item
     * @param slot the slot to drop the item from, -1 to drop the cursor item
     * @param wholeStack drop the whole stack or a single item
     * @param force If we should perform the drop even if the item is air for the client
     */
    public static void dropItem(int slot, boolean wholeStack, boolean force) {
        MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
        Minecraft minecraft = Minecraft.getInstance();
        AbstractContainerMenu menu = minecraft.player.containerMenu;
        if (menu == null) return;

        ClickType type = ClickType.THROW;

        if (slot < 0) {
            slot = -999;
            type = ClickType.PICKUP;
            if (!force && menu.getCarried().isEmpty()) return;
        } else if (!force && menu.slots.get(slot).getItem().isEmpty()) {
            return;
        }

        gameMode.handleInventoryMouseClick(menu.containerId, slot, wholeStack ? 0 : 1, type, minecraft.player);
    }

}
