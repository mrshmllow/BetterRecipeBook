package marsh.town.brb;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class InstantCraftingManager {
    public ItemStack lastCraft;
    private boolean startedDropCrafting;
    public boolean on;

    public InstantCraftingManager(boolean on) {
        this.on = on;
    }

    public void recipeClicked(Recipe<?> recipe) {
        if (BetterRecipeBook.instantCraftingManager.on) {
            lastCraft = recipe.getResultItem();
        }
    }

    public void onResultSlotUpdated(ItemStack itemStack) {
        if (lastCraft == null) return;
        if (itemStack.getItem() == Items.AIR) {
            if (startedDropCrafting) {
                lastCraft = null;
                startedDropCrafting = false;
            }
            return;
        }
        if (!itemStack.sameItemStackIgnoreDurability(lastCraft)) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.gameMode == null) return;
        if (client.screen == null) return;
        int syncId = ((AbstractContainerScreen<?>) client.screen).getMenu().containerId;
        client.gameMode.handleInventoryMouseClick(syncId, 0, 0, ClickType.QUICK_MOVE, client.player);
        lastCraft = null;
    }

    public boolean toggleOn() {
        this.on = !this.on;
        return this.on;
    }
}
