package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class UnlockRecipes {
    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    public void onPlayerConnect(ClientConnection con, ServerPlayerEntity player, CallbackInfo ci) {
        if (BetterRecipeBook.config.newRecipes.unlockAll) {
            player.unlockRecipes(player.server.getRecipeManager().values());
        }
        BetterRecipeBook.hasWarnedNoPermission = false;
    }
}
