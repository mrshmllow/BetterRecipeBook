package marsh.town.brb.mixins.instantcraft;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handlePlaceRecipe", at = @At("TAIL"))
    private void handlePlaceRecipe(int i, RecipeHolder<?> recipe, boolean bl, CallbackInfo ci) {
        BetterRecipeBook.instantCraftingManager.recipeClicked(recipe, minecraft.level.registryAccess());
    }
}