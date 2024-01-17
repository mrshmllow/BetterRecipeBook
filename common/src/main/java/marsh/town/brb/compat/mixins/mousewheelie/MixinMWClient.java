package marsh.town.brb.compat.mixins.mousewheelie;

import de.siphalor.mousewheelie.client.MWClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MWClient.class)
public class MixinMWClient {

    // mousewheelie implements scrolling in the recipe book. This breaks scroll in circles.
    // we should block mouse wheelies' scrolling impl as we implement one ourselves.
    @Inject(remap = false,
            method = "triggerScroll",
            at = @At(value = "INVOKE", target = "Lde/siphalor/mousewheelie/client/util/inject/IScrollableRecipeBook;mouseWheelie_onMouseScrollRecipeBook(DDD)Lde/siphalor/mousewheelie/client/util/ScrollAction;"),
            cancellable = true)
    private static void onTriggerScroll(double mouseX, double mouseY, double scrollY, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }

}
