package net.marshmallow.BetterRecipeBook.Mixins.Pins;

import com.google.common.collect.Lists;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.marshmallow.BetterRecipeBook.Mixins.Accessors.RecipeBookResultsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public abstract class Pins {
    @Shadow protected MinecraftClient client;
    @Nullable @Shadow private TextFieldWidget searchField;
    @Final @Shadow private RecipeBookResults recipesArea;

    @Shadow protected abstract void refreshResults(boolean resetCurrentPage);

    @Inject(method = "keyPressed", at = @At(value = "HEAD"))
    public void add(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_F && !this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
            for (AnimatedResultButton resultButton : ((RecipeBookResultsAccessor) this.recipesArea).getResultButtons()) {
                if (resultButton.isHovered()) {
                    BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavourite(resultButton.getResultCollection());
                    this.refreshResults(false);
                }
            }
        }
    }

    @Inject(method = "refreshResults", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookResults;setResults(Ljava/util/List;Z)V"))
    private void sort(boolean resetCurrentPage, CallbackInfo ci, List<RecipeResultCollection> list, List<RecipeResultCollection> list2) {
        List<RecipeResultCollection> list3 = Lists.newArrayList(list2);

        for (RecipeResultCollection recipeResultCollection : list3) {
            if (BetterRecipeBook.pinnedRecipeManager.has(recipeResultCollection)) {
                list2.remove(recipeResultCollection);
                list2.add(0, recipeResultCollection);
            }
        }

        list2 = list3;
    }
}