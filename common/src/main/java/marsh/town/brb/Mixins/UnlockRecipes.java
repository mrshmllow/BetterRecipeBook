package marsh.town.brb.Mixins;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Unlocks all recipes that are sent to the client or updated by either the local server or external server
 */
@Mixin(ClientPacketListener.class)
public class UnlockRecipes {

    @Inject(method = "handleAddOrRemoveRecipes", at = @At(value = "RETURN"))
    public void onAddOrRemoveRecipes(ClientboundRecipePacket clientboundRecipePacket, CallbackInfo ci) {
        unlockRecipesIfRequired();
    }

    @Inject(method = "handleUpdateRecipes", at = @At(value = "RETURN"))
    public void onUpdateRecipes(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
        unlockRecipesIfRequired();
    }

    private static void unlockRecipesIfRequired() {
        if (BetterRecipeBook.config.newRecipes.unlockAll) {
            unlockRecipes();
        }
    }

    /**
     * Unlocks all recipes that the RecipeManager knows of then updates any screen implementing RecipeUpdateListener
     */
    private static void unlockRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player != null && player.connection != null) {
            RecipeManager recipeManager = player.connection.getRecipeManager();
            ClientRecipeBook recipeBook = player.getRecipeBook();
            recipeManager.getRecipes().forEach(recipeBook::add);
            if (minecraft.screen instanceof RecipeUpdateListener rul) {
                rul.recipesUpdated();
            }
        }
    }

}
