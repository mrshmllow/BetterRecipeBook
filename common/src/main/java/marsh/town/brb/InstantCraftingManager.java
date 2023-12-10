package marsh.town.brb;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class InstantCraftingManager {

    // used to signify the last clicked crafted recipe before updateCollections was called
    public RecipeHolder<?> lastClickedRecipe = null;
    // updated by RecipeBookPageMixin
    public RecipeCollection lastClickedCollection = null;

    public StateSwitchingButton lastInstantCraftButton = null;

    public ItemStack lastCraftResult;
    public long lastContainerId = -1;

    public InstantCraftingManager() {
        BetterRecipeBook.configHolder.registerLoadListener((h, c) -> {
            if (lastInstantCraftButton != null) lastInstantCraftButton.setStateTriggered(isEnabled());
            return InteractionResult.SUCCESS;
        });
        BetterRecipeBook.configHolder.registerSaveListener((h, c) -> {
            if (lastInstantCraftButton != null) lastInstantCraftButton.setStateTriggered(isEnabled());
            return InteractionResult.SUCCESS;
        });
    }

    public void recipeClicked(RecipeHolder<?> recipe, RegistryAccess registryAccess) {
        if (isEnabled()) {
            Minecraft client = Minecraft.getInstance();
            if (!(client.screen instanceof AbstractContainerScreen<?> screen)) return;

            // Keep buttons stationary - remember last recipe clicked, so we know which one to display
            lastClickedRecipe = recipe;
            // Set last craft for onResultSlotUpdated
            lastCraftResult = recipe.value().getResultItem(registryAccess);
            // there is no way to know for sure when (or if) the server will set the result item, so we'll ignore results from different containerIds
            lastContainerId = screen.getMenu().containerId;
        } else {
            // if instantcraft was disabled and another recipe was clicked, clear the last pending instantcraft
            lastCraftResult = null;
        }
    }

    public void onResultSlotUpdated(ItemStack itemStack) {
        Minecraft client = Minecraft.getInstance();
        if (!isEnabled()
                || client.gameMode == null
                || !(client.screen instanceof AbstractContainerScreen<?> screen)) return;

        if (lastCraftResult == null
                || !ItemStack.isSameItemSameTags(itemStack, lastCraftResult)
                || lastContainerId != screen.getMenu().containerId) return;

        client.gameMode.handleInventoryMouseClick(screen.getMenu().containerId, 0, 0, ClickType.QUICK_MOVE, client.player);
        lastCraftResult = null;
    }

    public boolean toggleEnabled() {
        BetterRecipeBook.config.instantCraft.enabled = !BetterRecipeBook.config.instantCraft.enabled;
        BetterRecipeBook.configHolder.save();
        return isEnabled();
    }

    public boolean isEnabled() {
        return BetterRecipeBook.config.instantCraft.enabled;
    }

}
