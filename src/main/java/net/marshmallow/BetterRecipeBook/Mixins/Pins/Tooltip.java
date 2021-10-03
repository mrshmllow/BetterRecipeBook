package net.marshmallow.BetterRecipeBook.Mixins.Pins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(AnimatedResultButton.class)
public abstract class Tooltip {
    @Shadow public abstract RecipeResultCollection getResultCollection();

    @Inject(method = "getTooltip", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("RETURN"))
    public void getTooltip(Screen screen, CallbackInfoReturnable<List<Text>> cir, ItemStack itemStack, List<Text> list) {
        if (BetterRecipeBook.pinnedRecipeManager.has(this.getResultCollection())) {
            list.add(new TranslatableText("betterrecipebook.gui.pin.remove"));
        } else {
            list.add(new TranslatableText("betterrecipebook.gui.pin.add"));
        }
    }
}
