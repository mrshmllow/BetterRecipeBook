package marsh.town.brb.brewingstand;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.GenericRecipePage;
import marsh.town.brb.recipe.BRBRecipeBookCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.BrewingStandMenu;

@Environment(EnvType.CLIENT)
public class BrewingRecipeBookResults extends GenericRecipePage<BrewingStandMenu, BrewingRecipeCollection, BrewableResult, BrewableRecipeButton> {
    BRBRecipeBookCategory category;

    public BrewingRecipeBookResults(RegistryAccess registryAccess) {
        super(registryAccess);

        for (int i = 0; i < 20; ++i) {
            this.buttons.add(new BrewableRecipeButton(registryAccess));
        }
    }

    @Override
    protected boolean overlayMouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m) {
        return false;
    }

    @Override
    protected void initOverlay(BrewingRecipeCollection recipeCollection, int x, int y, RegistryAccess registryAccess) {
    }

    public void updateButtonsForPage() {
        int i = 20 * this.currentPage;

        for (int j = 0; j < this.buttons.size(); ++j) {
            BrewableRecipeButton brewableRecipeButton = this.buttons.get(j);
            if (i + j < this.recipeCollections.size()) {
                BrewingRecipeCollection output = this.recipeCollections.get(i + j);
                brewableRecipeButton.showCollection(output, menu, category);
                brewableRecipeButton.visible = true;
            } else {
                brewableRecipeButton.visible = false;
            }
        }

        this.updateArrowButtons();
    }

    @Override
    public boolean overlayIsVisible() {
        return false;
    }

    @Override
    public boolean isFilteringCraftable() {
        return BetterRecipeBook.rememberedBrewingToggle;
    }
}
