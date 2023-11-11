package marsh.town.brb.brewingstand;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.GenericRecipePage;
import marsh.town.brb.recipe.BRBRecipeBookCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.BrewingStandMenu;

import java.util.Iterator;

@Environment(EnvType.CLIENT)
public class BrewingRecipeBookResults extends GenericRecipePage<BrewingStandMenu, BrewingRecipeCollection, BrewableResult, BrewableRecipeButton> {
    BRBRecipeBookCategory category;

    public BrewingRecipeBookResults(RegistryAccess registryAccess) {
        super();

        for (int i = 0; i < 20; ++i) {
            this.buttons.add(new BrewableRecipeButton(registryAccess));
        }
    }

    @Override
    public void initialize(Minecraft client, int parentLeft, int parentTop, BrewingStandMenu menu, int leftOffset) {
        super.initialize(client, parentLeft, parentTop, menu, leftOffset);

        // this.recipeBook = client.player.getRecipeBook();

        for (int k = 0; k < this.buttons.size(); ++k) {
            this.buttons.get(k).setPosition(parentLeft + 11 + 25 * (k % 5), parentTop + 31 + 25 * (k / 5));
        }
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
    public boolean mouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeCollection = null;
        if (this.forwardButton.mouseClicked(mouseX, mouseY, button)) {
            if (++currentPage >= totalPages) {
                currentPage = BetterRecipeBook.config.scrolling.scrollAround ? 0 : totalPages - 1;
            }
            this.updateButtonsForPage();
            return true;
        } else if (this.backButton.mouseClicked(mouseX, mouseY, button)) {
            if (--currentPage < 0) {
                currentPage = BetterRecipeBook.config.scrolling.scrollAround ? totalPages - 1 : 0;
            }
            this.updateButtonsForPage();
            return true;
        } else {
            Iterator<BrewableRecipeButton> var10 = this.buttons.iterator();

            BrewableRecipeButton brewableRecipeButton;
            do {
                if (!var10.hasNext()) {
                    return false;
                }

                brewableRecipeButton = var10.next();
            } while (!brewableRecipeButton.mouseClicked(mouseX, mouseY, button));

            if (button == 0) {
                this.lastClickedRecipeCollection = brewableRecipeButton.getCollection();
                this.lastClickedRecipe = this.lastClickedRecipeCollection.getFirst();
            }

            return true;
        }
    }

    @Override
    public void drawTooltip(GuiGraphics gui, int x, int y) {
        if (this.minecraft.screen != null && hoveredButton != null) {
            gui.renderComponentTooltip(Minecraft.getInstance().font, this.hoveredButton.getTooltipText(), x, y);
        }
    }

    @Override
    public boolean overlayIsVisible() {
        return false;
    }

    @Override
    public boolean isFilteringCraftable() {
        return BetterRecipeBook.rememberedBrewingToggle;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(gui, x, y, mouseX, mouseY, delta);

        this.hoveredButton = null;

        for (BrewableRecipeButton brewableRecipeButton : this.buttons) {
            brewableRecipeButton.render(gui, mouseX, mouseY, delta);
            if (brewableRecipeButton.visible && brewableRecipeButton.isHoveredOrFocused()) {
                this.hoveredButton = brewableRecipeButton;
            }
        }

        this.backButton.render(gui, mouseX, mouseY, delta);
        this.forwardButton.render(gui, mouseX, mouseY, delta);
        // this.alternatesWidget.render(matrices, mouseX, mouseY, delta);
    }
}
