package marsh.town.brb.mixins.pins;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.mixins.accessors.RecipeBookPageAccessor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookComponentMixin {

    @Shadow @Final private RecipeBookPage recipeBookPage;

    @Inject(method = "updateCollections", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;Z)V"))
    private void updateCollections(boolean bl, CallbackInfo ci, List<RecipeCollection> list, List<RecipeCollection> list2) {
        if (!BetterRecipeBook.config.enablePinning) return;

        List<RecipeCollection> list2copy = Lists.newArrayList(list2);

        for (RecipeCollection collection : list2copy) {
            if (BetterRecipeBook.pinnedRecipeManager.has(collection)) {
                list2.remove(collection);
                list2.add(0, collection);
            }
        }

        if (BetterRecipeBook.instantCraftingManager.on && this.recipeBookPage != null) {
            List<RecipeButton> buttons = ((RecipeBookPageAccessor) recipeBookPage).getButtons();
            RecipeButton btn = buttons.stream().filter(AbstractWidget::isHovered).findAny().orElse(null);
            if (btn != null) {
                RecipeCollection hoveredCollection = btn.getCollection();
                int idx = ((RecipeBookPageAccessor) recipeBookPage).getCollections().indexOf(hoveredCollection);
                if (idx != -1 && idx < list2.size()) {
                    BetterRecipeBook.currentHoveredRecipeCollection = hoveredCollection;
                    list2.remove(hoveredCollection);
                    list2.add(idx, hoveredCollection);
                }
            }
        }
    }

}
