package marsh.town.brb.Mixins.unlockrecipes;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.Mixins.Accessors.RecipeBookComponentAccessor;
import marsh.town.brb.util.ClientInventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handlePlaceRecipe", at = @At(value = "HEAD"))
    public void onPlaceRecipe(int z, Recipe<?> recipe, boolean bl, CallbackInfo ci) {
        if (BetterRecipeBook.config.newRecipes.unlockAll && minecraft.player != null &&
                minecraft.screen instanceof RecipeUpdateListener rul && minecraft.player.containerMenu instanceof RecipeBookMenu<?> menu) {
            RecipeBookComponent comp = rul.getRecipeBookComponent();

            // if we don't have all the items place a client side ghost recipe
            RecipeBookPage page = ((RecipeBookComponentAccessor) comp).getRecipeBookPage();
            RecipeCollection lastRecipe = page.getLastClickedRecipeCollection();
            StackedContents contents = new StackedContents();
            for (Slot slot : menu.slots) {
                if (slot.index != menu.getResultSlotIndex()) contents.accountStack(slot.getItem());
            }
            lastRecipe.canCraft(contents, menu.getGridWidth(), menu.getGridHeight(), minecraft.player.getRecipeBook());

            if (!lastRecipe.isCraftable(recipe)) {
                // remove items from the crafting grid: not all backends do this for us if we haven't unlocked the recipe
                for (int i = menu.getResultSlotIndex(); i < menu.getSize(); i++) {
                    ClientInventoryUtil.storeItem(i, idx -> idx < menu.getResultSlotIndex() || idx >= menu.getSize());
                }

                // place the ghost recipe as we can't craft the recipe yet
                comp.setupGhostRecipe(recipe, menu.slots);
            }
        }
    }

}
