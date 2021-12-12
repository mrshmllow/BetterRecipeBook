package net.marshmallow.BetterRecipeBook.Mixins.Pins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AnimatedResultButton.class)
public class Icon {
    @Shadow private RecipeResultCollection resultCollection;

    @Shadow private float bounce;

    @ModifyArg(method = "renderButton", index = 1, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V"))
    public Identifier setIcon(Identifier identifier) {
        if (!BetterRecipeBook.config.enablePinning) return identifier;
        return BetterRecipeBook.pinnedRecipeManager.has(this.resultCollection) ? new Identifier("betterrecipebook:textures/gui/pinned.png") : identifier;
    }

    @ModifyArg(method = "renderButton", index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/AnimatedResultButton;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public int fixX(int i) {
        if (!BetterRecipeBook.config.enablePinning) return i;
        return BetterRecipeBook.pinnedRecipeManager.has(this.resultCollection) ? i - 29 : i;
    }

    @ModifyArg(method = "renderButton", index = 4, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/AnimatedResultButton;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public int fixY(int j) {
        if (!BetterRecipeBook.config.enablePinning) return j;

        boolean bl = BetterRecipeBook.pinnedRecipeManager.has(this.resultCollection);

        if (BetterRecipeBook.config.darkMode && bl) {
            j = j + 50;
        }

        return bl ? j - 206 : j;
    }
}
