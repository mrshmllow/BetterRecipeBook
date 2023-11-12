package marsh.town.brb.mixins.pins;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.interfaces.unlockrecipes.IMixinRecipeManager;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;

@Mixin(RecipeButton.class)
public abstract class RecipeButtonMixin extends AbstractWidget {

    protected RecipeButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Shadow
    public abstract RecipeCollection getCollection();

    @Shadow
    public abstract RecipeHolder<?> getRecipe();

    @Inject(method = "getTooltipText", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("RETURN"))
    public void getTooltip(CallbackInfoReturnable<List<Component>> cir, ItemStack itemStack, List<Component> list) {
        if (!BetterRecipeBook.config.enablePinning) return;

        Set<ResourceLocation> serverUnlockedRecipes = ((IMixinRecipeManager) Minecraft.getInstance().getConnection().getRecipeManager()).betterRecipeBook$getServerUnlockedRecipes();

        if (!serverUnlockedRecipes.contains(this.getRecipe().id()) && Minecraft.getInstance().screen != null) {
            if (((AbstractContainerScreen<?>) Minecraft.getInstance().screen).getMenu() instanceof AbstractFurnaceMenu) {
                list.add(0, Component.translatable("brb.gui.furnace.lockedRecipe").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            } else {
                list.add(0, Component.translatable("brb.gui.crafting.lockedRecipe").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            }
        }

        if (BetterRecipeBook.pinnedRecipeManager.has(this.getCollection())) {
            list.add(Component.translatable("brb.gui.pin.remove"));
        } else {
            list.add(Component.translatable("brb.gui.pin.add"));
        }
    }

    @Inject(method = "renderWidget", at = @At(value = "RETURN", target = "Lnet/minecraft/client/gui/GuiGraphics;renderFakeItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    public void renderWidget_renderFakeItem(GuiGraphics gui, int x, int y, float delta, CallbackInfo ci) {
        // if pins are enabled, and the recipe is pinned, blit the pin texture after the recipe collection is rendered
        if (BetterRecipeBook.config.enablePinning && BetterRecipeBook.pinnedRecipeManager.has(getCollection())) {
            gui.blitSprite(BRBTextures.RECIPE_BOOK_PIN_SPRITE, getX() - 4, getY() - 4, 32, 32);
        }
    }

}
