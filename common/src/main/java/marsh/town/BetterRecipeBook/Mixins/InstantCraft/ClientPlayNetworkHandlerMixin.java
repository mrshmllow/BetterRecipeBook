package marsh.town.BetterRecipeBook.Mixins.InstantCraft;

import marsh.town.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"), method = "handleContainerSetSlot")
    private void onScreenHandlerSlotUpdate(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        if (packet.getSlot() != 0) return;
        if (packet.getItem() == null) return;
        BetterRecipeBook.instantCraftingManager.onResultSlotUpdated(packet.getItem());
    }
}
