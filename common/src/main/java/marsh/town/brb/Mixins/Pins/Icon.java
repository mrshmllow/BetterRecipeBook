package marsh.town.brb.Mixins.Pins;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RecipeButton.class)
public class Icon {
    @Shadow private RecipeCollection collection;

    @Shadow private float animationTime;

    @ModifyArg(method = "renderWidget", index = 1, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"))
    public ResourceLocation setIcon(ResourceLocation identifier) {
        if (!BetterRecipeBook.config.enablePinning) return identifier;
        return BetterRecipeBook.pinnedRecipeManager.has(this.collection) ? new ResourceLocation("brb:textures/gui/pinned.png") : identifier;
    }

    @ModifyArg(method = "renderWidget", index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeButton;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"))
    public int fixX(int i) {
        if (!BetterRecipeBook.config.enablePinning) return i;
        return BetterRecipeBook.pinnedRecipeManager.has(this.collection) ? i - 29 : i;
    }

    @ModifyArg(method = "renderWidget", index = 4, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeButton;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"))
    public int fixY(int j) {
        if (!BetterRecipeBook.config.enablePinning) return j;

        boolean bl = BetterRecipeBook.pinnedRecipeManager.has(this.collection);

        if (BetterRecipeBook.config.darkMode && bl) {
            j = j + 50;
        }

        return bl ? j - 206 : j;
    }
}
