package net.marshmallow.BetterRecipeBook;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.SlotActionType;

public class InstantCraftingManager {
    public ItemStack lastCraft;
    private boolean startedDropCrafting;
    public boolean on;

    public InstantCraftingManager(boolean on) {
        this.on = on;
    }

    public void recipeClicked(Recipe<?> recipe) {
        if (BetterRecipeBook.instantCraftingManager.on) {
            lastCraft = recipe.getOutput();
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
        if (!itemStack.isItemEqual(lastCraft)) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.interactionManager == null) return;
        if (client.currentScreen == null) return;
        int syncId = ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId;
        client.interactionManager.clickSlot(syncId, 0, 0, SlotActionType.QUICK_MOVE, client.player);
        lastCraft = null;
    }

    public boolean toggleOn() {
        this.on = !this.on;
        return this.on;
    }
}
