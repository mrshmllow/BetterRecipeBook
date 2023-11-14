package marsh.town.brb.mixins.unlockrecipes;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.interfaces.unlockrecipes.IMixinRecipeManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;

@Mixin(RecipeButton.class)
public abstract class RecipeButtonMixin {
    @Shadow
    public abstract Recipe<?> getRecipe();

    @Inject(method = "getTooltipText", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("RETURN"))
    public void getTooltip(CallbackInfoReturnable<List<Component>> cir, ItemStack itemStack, List<Component> list) {
        if (!BetterRecipeBook.config.newRecipes.unlockAll) return;

        Set<ResourceLocation> serverUnlockedRecipes = ((IMixinRecipeManager) Minecraft.getInstance().getConnection().getRecipeManager()).betterRecipeBook$getServerUnlockedRecipes();

        if (!serverUnlockedRecipes.contains(this.getRecipe().getId()) && Minecraft.getInstance().screen != null) {
            if (((AbstractContainerScreen<?>) Minecraft.getInstance().screen).getMenu() instanceof AbstractFurnaceMenu) {
                list.add(0, Component.translatable("brb.gui.furnace.lockedRecipe").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            } else {
                list.add(0, Component.translatable("brb.gui.crafting.lockedRecipe").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            }
        }
    }
}
