package marsh.town.brb.Mixins.unlockrecipes;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(at = @At(value = "HEAD"), method = "handlePlaceRecipe")
    public void onPlaceRecipe(int i, Recipe<?> recipe, boolean bl, CallbackInfo ci) {
        // based off of ClientPacketListener#handlePlaceRecipe(ClientboundPlaceGhostRecipePacket)
        if (BetterRecipeBook.config.newRecipes.unlockAll && minecraft.player != null && minecraft.screen instanceof RecipeUpdateListener rul) {
            // after this we need to listener for when the server update the inventory and check what slots are non-empty and hide the ghost recipe
            rul.getRecipeBookComponent().setupGhostRecipe(recipe, minecraft.player.containerMenu.slots);
        }
    }

}
