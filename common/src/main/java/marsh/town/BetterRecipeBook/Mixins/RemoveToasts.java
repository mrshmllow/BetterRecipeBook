package marsh.town.BetterRecipeBook.Mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import marsh.town.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeToast.class)
public class RemoveToasts {
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void draw(PoseStack matrices, ToastComponent manager, long startTime, CallbackInfoReturnable<Toast.Visibility> cir) {
        if (BetterRecipeBook.config.newRecipes.unlockAll) {
            cir.setReturnValue(Toast.Visibility.HIDE);
        }
    }
}
