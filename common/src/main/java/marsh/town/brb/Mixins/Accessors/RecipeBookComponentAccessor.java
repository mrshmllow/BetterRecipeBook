package marsh.town.brb.Mixins.Accessors;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentAccessor {

    @Accessor("ghostRecipe")
    GhostRecipe getGhostRecipe();

    @Accessor("recipeBookPage")
    RecipeBookPage getRecipeBookPage();

    @Accessor("searchBox")
    void setSearchBox(EditBox searchBox);

}
