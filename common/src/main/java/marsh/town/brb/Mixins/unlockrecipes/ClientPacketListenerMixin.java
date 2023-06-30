package marsh.town.brb.Mixins.unlockrecipes;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.Mixins.Accessors.RecipeBookComponentAccessor;
import marsh.town.brb.util.RecipeMenuUtil;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Unlocks all recipes that are sent to the client or updated by either the local server or external server
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handleAddOrRemoveRecipes", at = @At(value = "RETURN"))
    public void onAddOrRemoveRecipes(ClientboundRecipePacket clientboundRecipePacket, CallbackInfo ci) {
        unlockRecipesIfRequired();
    }

    @Inject(method = "handleUpdateRecipes", at = @At(value = "RETURN"))
    public void onUpdateRecipes(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
        unlockRecipesIfRequired();
    }

    @Inject(method = "handleContainerSetSlot", at = @At(value = "HEAD"))
    public void onContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        // clear ghost recipes if crafting grid contents are changed by server
        if (BetterRecipeBook.config.newRecipes.unlockAll && minecraft.player != null
                && minecraft.player.containerMenu instanceof RecipeBookMenu<?> menu
                && minecraft.player.containerMenu.containerId == packet.getContainerId()
                && minecraft.screen instanceof RecipeUpdateListener rul) {
            if (!packet.getItem().isEmpty() && RecipeMenuUtil.isCraftingGridSlot(menu, packet.getSlot())) {
                ((RecipeBookComponentAccessor) rul.getRecipeBookComponent()).getGhostRecipe().clear();
            }
        }
    }

    @Inject(method = "handlePlaceRecipe", at = @At(value = "HEAD", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;setupGhostRecipe(Lnet/minecraft/world/item/crafting/Recipe;Ljava/util/List;)V"))
    public void onHandlePlaceRecipe_setupGhostRecipe(ClientboundPlaceGhostRecipePacket packet, CallbackInfo ci) {
        if (minecraft.screen instanceof RecipeUpdateListener rul) {
            ((RecipeBookComponentAccessor) rul.getRecipeBookComponent()).getGhostRecipe().clear();
        }
    }


    private void unlockRecipesIfRequired() {
        if (BetterRecipeBook.config.newRecipes.unlockAll) {
            unlockRecipes();
        }
    }

    /**
     * Unlocks all recipes that the RecipeManager knows of, then updates any screen implementing RecipeUpdateListener
     */
    private void unlockRecipes() {
        LocalPlayer player = minecraft.player;
        if (player != null && player.connection != null) {
            RecipeManager recipeManager = player.connection.getRecipeManager();
            ClientRecipeBook recipeBook = player.getRecipeBook();
            recipeManager.getRecipes().forEach(recipeBook::add);
            recipeBook.getCollections().forEach(recipeCollection -> recipeCollection.updateKnownRecipes(recipeBook));
            if (minecraft.screen instanceof RecipeUpdateListener rul) {
                rul.recipesUpdated();
            }
        }
    }

}
