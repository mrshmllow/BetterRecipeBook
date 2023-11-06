package marsh.town.brb.mixins.unlockrecipes;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.interfaces.unlockrecipes.IMixinRecipeManager;
import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
import marsh.town.brb.util.RecipeMenuUtil;
import marsh.town.brb.util.RecipeUnlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

/**
 * Unlocks all recipes that are sent to the client or updated by either the local server or external server
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow @Final private RecipeManager recipeManager;
    @Unique private Minecraft _$minecraft = Minecraft.getInstance();

    @Inject(method = "handleAddOrRemoveRecipes", at = @At(value = "RETURN"))
    public void onAddOrRemoveRecipes(ClientboundRecipePacket packet, CallbackInfo ci) {
        //System.out.println("addOrRemoveRecipes %s: %s".formatted(packet.getState(), Joiner.on(", ").join(packet.getRecipes())));
        Set<ResourceLocation> serverUnlockedRecipes = ((IMixinRecipeManager) recipeManager)._$getServerUnlockedRecipes();
        switch (packet.getState()) {
            case INIT:
                serverUnlockedRecipes.clear();
            case ADD:
                serverUnlockedRecipes.addAll(packet.getRecipes());
                break;
            case REMOVE:
                packet.getRecipes().forEach(serverUnlockedRecipes::remove);
        }
        RecipeUnlockUtil.unlockRecipesIfRequired();
    }

    @Inject(method = "handleUpdateRecipes", at = @At(value = "RETURN"))
    public void onUpdateRecipes(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
        RecipeUnlockUtil.unlockRecipesIfRequired();
    }

    @Inject(method = "handleContainerSetSlot", at = @At(value = "HEAD"))
    public void onContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        // clear ghost recipes if crafting grid contents are changed by server
        if (BetterRecipeBook.config.newRecipes.unlockAll && _$minecraft.player != null
                && _$minecraft.player.containerMenu instanceof RecipeBookMenu<?> menu
                && _$minecraft.player.containerMenu.containerId == packet.getContainerId()
                && _$minecraft.screen instanceof RecipeUpdateListener rul) {
            if (!packet.getItem().isEmpty() && RecipeMenuUtil.isRecipeSlot(menu, packet.getSlot())) {
                ((RecipeBookComponentAccessor) rul.getRecipeBookComponent()).getGhostRecipe().clear();
            }
        }
    }

    @Inject(method = "handlePlaceRecipe", at = @At(value = "HEAD", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;setupGhostRecipe(Lnet/minecraft/world/item/crafting/Recipe;Ljava/util/List;)V"))
    public void onHandlePlaceRecipe_setupGhostRecipe(ClientboundPlaceGhostRecipePacket packet, CallbackInfo ci) {
        if (_$minecraft.screen instanceof RecipeUpdateListener rul) {
            ((RecipeBookComponentAccessor) rul.getRecipeBookComponent()).getGhostRecipe().clear();
        }
    }


}
