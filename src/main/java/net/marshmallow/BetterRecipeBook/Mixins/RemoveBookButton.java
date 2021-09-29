package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class RemoveBookButton {
    // The targeting is not particularly elegant
    @Inject(method = "init", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;findLeftEdge(II)I"), cancellable = true)
    private void mixin(CallbackInfo ci) {
        if (!BetterRecipeBook.config.enableBook) {
            ci.cancel();
        }
    }
}
