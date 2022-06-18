package marsh.town.brb.Mixins.Pins;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(RecipeButton.class)
public abstract class Tooltip {
    @Shadow public abstract RecipeCollection getCollection();

    @Inject(method = "getTooltipText", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("RETURN"))
    public void getTooltip(Screen screen, CallbackInfoReturnable<List<Component>> cir, ItemStack itemStack, List<Component> list) {
        if (!BetterRecipeBook.config.enablePinning) return;

        if (BetterRecipeBook.pinnedRecipeManager.has(this.getCollection())) {
            list.add(Component.translatable("brb.gui.pin.remove"));
        } else {
            list.add(Component.translatable("brb.gui.pin.add"));
        }
    }
}
