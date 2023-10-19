package marsh.town.brb.mixins.ungroup;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Locale;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
    @Shadow private String lastSearch;
    @Shadow private ClientRecipeBook book;
    @Shadow protected RecipeBookMenu<?> menu;
    @Shadow @Final private RecipeBookPage recipeBookPage;

    @Inject(method = "updateCollections", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;<init>(Ljava/util/Collection;)V"), cancellable = true)
    private void refreshSearchResults(boolean bl, CallbackInfo ci, List<RecipeCollection> list, List<RecipeCollection> list2, String string) {
        if (BetterRecipeBook.config.alternativeRecipes.noGrouped) {
            list2.removeIf((recipeResultCollection) -> {
                for (RecipeHolder<?> recipe : recipeResultCollection.getRecipes()) {
                    return !recipe.value().getResultItem(recipeResultCollection.registryAccess()).getHoverName().getString().toLowerCase(Locale.ROOT).contains(this.lastSearch.toLowerCase(Locale.ROOT));
                }
                return false;
            });

            if (this.book.isFiltering(this.menu)) {
                list2.removeIf((recipeCollection) -> !recipeCollection.hasCraftable());
            }

            this.recipeBookPage.updateCollections(list2, bl);
            ci.cancel();
        }
    }
}