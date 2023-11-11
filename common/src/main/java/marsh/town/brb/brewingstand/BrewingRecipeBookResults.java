package marsh.town.brb.brewingstand;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.GenericRecipePage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.BrewingStandMenu;

@Environment(EnvType.CLIENT)
public class BrewingRecipeBookResults extends GenericRecipePage<BrewingStandMenu, BrewingRecipeCollection, BrewableResult, BrewableRecipeButton> {
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

    @Override
    public boolean overlayIsVisible() {
        return false;
    }

    @Override
    public boolean isFilteringCraftable() {
        return BetterRecipeBook.rememberedBrewingToggle;
    }
}
