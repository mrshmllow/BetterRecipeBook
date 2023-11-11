package marsh.town.brb.smithingtable;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.GenericRecipePage;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.SmithingMenu;

public class SmithingRecipeBookPage extends GenericRecipePage<SmithingMenu, SmithingRecipeCollection, BRBSmithingRecipe, SmithableRecipeButton> {
    public final SmithingOverlayRecipeComponent overlay = new SmithingOverlayRecipeComponent();

    public SmithingRecipeBookPage(RegistryAccess registryAccess) {
        super(registryAccess);

        for (int i = 0; i < 20; ++i) {
            this.buttons.add(new SmithableRecipeButton(registryAccess));
        }
    }

    @Override
    protected boolean overlayMouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m) {
        if (this.overlay.mouseClicked(mouseX, mouseY, button)) {
            this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
            this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
        } else {
            this.overlay.setVisible(false);
        }
        return true;
    }

    @Override
    protected void initOverlay(SmithingRecipeCollection recipeCollection, int x, int y, RegistryAccess registryAccess) {
        this.overlay.init(recipeCollection, this.parentLeft, this.parentTop, registryAccess);
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(gui, x, y, mouseX, mouseY, delta);

        this.overlay.render(gui, mouseX, mouseY, delta);
    }

    @Override
    public boolean overlayIsVisible() {
        return this.overlay.isVisible();
    }

    @Override
    public boolean isFilteringCraftable() {
        return BetterRecipeBook.rememberedSmithableToggle;
    }
}
