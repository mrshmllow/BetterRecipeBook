package marsh.town.brb.mixins.unlockrecipes;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
import marsh.town.brb.util.ClientInventoryUtil;
import marsh.town.brb.util.RecipeMenuUtil;
import marsh.town.brb.util.RecipePlacement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handlePlaceRecipe", at = @At(value = "HEAD"), cancellable = true)
    public void onPlaceRecipe(int z, RecipeHolder<?> recipe, boolean bl, CallbackInfo ci) {
        if (BetterRecipeBook.config.newRecipes.unlockAll && minecraft.player != null &&
                minecraft.screen instanceof RecipeUpdateListener rul && minecraft.player.containerMenu instanceof RecipeBookMenu<?> menu) {
            RecipeBookComponent comp = rul.getRecipeBookComponent();

            RecipeBookPage page = ((RecipeBookComponentAccessor) comp).getRecipeBookPage();
            RecipeCollection lastRecipe = page.getLastClickedRecipeCollection();
            StackedContents contents = new StackedContents();
            for (Slot slot : menu.slots) {
                if (slot.index != menu.getResultSlotIndex()) contents.accountStack(slot.getItem());
            }
            lastRecipe.canCraft(contents, menu.getGridWidth(), menu.getGridHeight(), minecraft.player.getRecipeBook());

            // if we don't have all the items place a client side ghost recipe
            if (!lastRecipe.isCraftable(recipe)) {
                // remove items from the crafting grid: not all backends do this for us if we haven't unlocked the recipe
                _$clearCraftingGrid(menu);

                // place the ghost recipe as we can't craft the recipe yet
                comp.setupGhostRecipe(recipe, menu.slots);
            } else if (BetterRecipeBook.config.newRecipes.forcePlaceRecipes) { // otherwise, if we have forcePlaceRecipes enabled, manually place the recipe
                MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;

                // remove items from the crafting grid
                _$clearCraftingGrid(menu);

                // we are placing the recipe ourselves, don't ask the server to do it.
                ci.cancel();

                // drop held item if required
                if (!menu.getCarried().isEmpty()) ClientInventoryUtil.dropItem(-1, true, false);

                // place the recipe in a dummy array to get the correct indexes and ingredients
                var placement = RecipePlacement.create(recipe, menu.getGridWidth(), menu.getGridHeight());
                // TODO debug
                System.out.println(placement);

                for (Slot craftingSlot : menu.slots) {
                    if (RecipeMenuUtil.isCraftingGridSlot(menu, craftingSlot.index)) {
                        // TODO debug
                        System.out.println("run for craftingSlot %d".formatted(craftingSlot.index));
                        // get the ingredients for this slot in the crafting grid
                        // we need to adjust out the offset of the result slot when getting the ingredient
                        var slotIngredients = placement.get(craftingSlot.index - (1 + menu.getResultSlotIndex()));

                        for (Slot inventorySlot : menu.slots) {
                            // ignore crafting slots to prevent us from taking recipe items already in the crafting grid
                            if (!RecipeMenuUtil.isCraftingGridSlot(menu, inventorySlot.index)) {
                                // check if any ingredient is compatible with the item in this inventory slot
                                if (slotIngredients.stream().anyMatch(i -> i.test(inventorySlot.getItem()))) {
                                    // TODO debug
                                    System.out.println("pickup %s at %d, place at %d".formatted(inventorySlot.getItem(), inventorySlot.index, craftingSlot.index));
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

                // if instant craft is enabled, set last craft
                if (BetterRecipeBook.instantCraftingManager.on) {
                    BetterRecipeBook.instantCraftingManager.recipeClicked(recipe.value(), minecraft.level.registryAccess());
                }
            }
        }
    }

    @Unique
    private static void _$clearCraftingGrid(RecipeBookMenu<?> menu) {
        for (int i = menu.getResultSlotIndex() + 1; i < menu.getSize(); i++) {
            ClientInventoryUtil.storeItem(i, idx -> idx < menu.getResultSlotIndex() || idx >= menu.getSize());
        }
    }

}
