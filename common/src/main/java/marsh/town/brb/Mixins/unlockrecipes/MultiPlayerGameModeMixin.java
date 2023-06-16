package marsh.town.brb.Mixins.unlockrecipes;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.Mixins.Accessors.CraftingMenuAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.inventory.CraftingMenu;
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
    public void onPlaceRecipe(int i, Recipe<?> recipe, boolean bl, CallbackInfo ci) {
        // based off of ClientPacketListener#handlePlaceRecipe(ClientboundPlaceGhostRecipePacket)
        if (BetterRecipeBook.config.newRecipes.unlockAll && minecraft.player != null &&
                minecraft.screen instanceof RecipeUpdateListener rul && minecraft.player.containerMenu instanceof CraftingMenu craftingMenu) {
            // don't place ghost items if items are in the crafting grid
            // TODO find a better solution?
            if (!(craftingMenu instanceof CraftingMenuAccessor cma && cma.getCraftingContainer().isEmpty())) return;

            // after this we need to listener for when the server update the inventory and check what slots are non-empty and hide the ghost recipe
            rul.getRecipeBookComponent().setupGhostRecipe(recipe, craftingMenu.slots);
        }
    }

}
