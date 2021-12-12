package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class UnlockRecipes {
    @Inject(at = @At("RETURN"), method = "placeNewPlayer")
    public void onPlayerConnect(Connection con, ServerPlayer player, CallbackInfo ci) {
        if (BetterRecipeBook.config.newRecipes.unlockAll) {
            player.awardRecipes(player.server.getRecipeManager().getRecipes());
        }
        BetterRecipeBook.hasWarnedNoPermission = false;
    }
}
