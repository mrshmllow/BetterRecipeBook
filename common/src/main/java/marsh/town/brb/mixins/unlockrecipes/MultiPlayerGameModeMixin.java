package marsh.town.brb.mixins.unlockrecipes;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.interfaces.unlockrecipes.IMixinRecipeManager;
import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
import marsh.town.brb.util.ClientInventoryUtil;
import marsh.town.brb.util.RecipeMenuUtil;
import marsh.town.brb.util.RecipePlacement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void startPrediction(ClientLevel arg, PredictiveAction arg2);

    @Shadow
    private int carriedIndex;

    @Inject(method = "handlePlaceRecipe", at = @At(value = "HEAD"), cancellable = true)
    public void onPlaceRecipe(int z, Recipe<?> recipe, boolean shiftKeyDown, CallbackInfo ci) {
        if (BetterRecipeBook.config.newRecipes.unlockAll && minecraft.player != null && minecraft.gameMode != null && minecraft.getConnection() != null &&
                minecraft.screen instanceof RecipeUpdateListener rul && minecraft.player.containerMenu instanceof RecipeBookMenu<?> menu) {
            RecipeBookComponent comp = rul.getRecipeBookComponent();

            RecipeBookPage page = ((RecipeBookComponentAccessor) comp).getRecipeBookPage();
            RecipeCollection lastRecipe = page.getLastClickedRecipeCollection();
            StackedContents contents = new StackedContents();
            for (Slot slot : menu.slots) {
                if (slot.index != menu.getResultSlotIndex()) contents.accountStack(slot.getItem());
            }
            lastRecipe.canCraft(contents, menu.getGridWidth(), menu.getGridHeight(), minecraft.player.getRecipeBook());

            Set<ResourceLocation> serverUnlockedRecipes = ((IMixinRecipeManager) minecraft.getConnection().getRecipeManager()).betterRecipeBook$getServerUnlockedRecipes();

            // if we don't have all the items place a client side ghost recipe
            if (!lastRecipe.isCraftable(recipe)) {
                // remove items from the crafting grid: not all backends do this for us if we haven't unlocked the recipe
                for (int i = 0; i < menu.getSize() && i != menu.getResultSlotIndex(); i++) {
                    ClientInventoryUtil.storeItem(i, idx -> idx != menu.getResultSlotIndex() || idx >= menu.getSize());
                }

                // place the ghost recipe as we can't craft the recipe yet
                comp.setupGhostRecipe(recipe, menu.slots);

                // don't send recipe requests to the server that we don't have
                if (!serverUnlockedRecipes.contains(recipe.getId())) ci.cancel();
            } else if (!serverUnlockedRecipes.contains(recipe.getId())) { // if the server didn't unlock this recipe for us, manually place the recipe
                MultiPlayerGameMode gameMode = minecraft.gameMode;

                // we are placing the recipe ourselves, don't ask the server to do it.
                ci.cancel();

                // store/drop held item if required
                if (!menu.getCarried().isEmpty())
                    ClientInventoryUtil.storeItem(-1, idx -> idx != menu.getResultSlotIndex() || idx >= menu.getSize());

                // get the recipe placement to use to filter items and place them in the crafting grid
                var placement = RecipePlacement.create(recipe, menu.getGridWidth(), menu.getGridHeight());
                //System.out.println("placement: " + Joiner.on(", ").join(placement.stream().map(s -> s.stream().map(i -> i.toJson(true)).toList()).toList()));

                for (Slot craftingSlot : menu.slots) {
                    if (RecipeMenuUtil.isRecipeSlot(menu, craftingSlot.index)) {
                        // get the ingredients for this slot in the crafting grid
                        // we need to adjust out the offset of the result slot when getting the ingredient for crafting tables
                        var slotIngredients = placement.get(craftingSlot.index - (menu.getResultSlotIndex() > 0 ? 0 : 1));

                        // if the item in the crafting grid doesn't match the recipe, remove it.
                        if (!craftingSlot.getItem().isEmpty() && (slotIngredients.isEmpty() || slotIngredients.stream().anyMatch(i -> !i.test(craftingSlot.getItem())))) {
                            ClientInventoryUtil.storeItem(craftingSlot.index, idx -> idx != menu.getResultSlotIndex() || idx >= menu.getSize());
                        }

                        // find items to put in the crafting grid
                        for (Slot inventorySlot : menu.slots) {
                            // ignore crafting slots to prevent us from taking recipe items already in the crafting grid
                            if (!RecipeMenuUtil.isRecipeSlot(menu, inventorySlot.index)) {
                                // check if any ingredient is compatible with the item in this inventory slot
                                if (slotIngredients.stream().anyMatch(i -> i.test(inventorySlot.getItem()))) {
                                    // pickup whole stack, deposit one, then return remaining stack
                                    gameMode.handleInventoryMouseClick(menu.containerId, inventorySlot.index, 0, ClickType.PICKUP, minecraft.player);
                                    gameMode.handleInventoryMouseClick(menu.containerId, craftingSlot.index, 1, ClickType.PICKUP, minecraft.player);
                                    if (!menu.getCarried().isEmpty()) {
                                        gameMode.handleInventoryMouseClick(menu.containerId, inventorySlot.index, 0, ClickType.PICKUP, minecraft.player);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

                if (menu instanceof AbstractFurnaceMenu) {
                    // furnace menus are supposed to take out the result item when a recipe is placed
                    if (menu.getSlot(menu.getResultSlotIndex()).hasItem()) {
                        ClientInventoryUtil.storeItem(menu.getResultSlotIndex(), idx -> idx != menu.getResultSlotIndex() || idx >= menu.getSize());
                    }
                } else {
                    // if instant craft is enabled, set last craft
                    if (BetterRecipeBook.instantCraftingManager.isEnabled() && !menu.getSlot(menu.getResultSlotIndex()).getItem().isEmpty()) {
                        BetterRecipeBook.instantCraftingManager.recipeClicked(recipe, minecraft.level.registryAccess());
                    }
                }
            }
        }
    }

}
