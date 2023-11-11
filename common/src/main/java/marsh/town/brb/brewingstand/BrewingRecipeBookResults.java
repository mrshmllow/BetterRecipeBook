package marsh.town.brb.brewingstand;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.GenericRecipePage;
import marsh.town.brb.recipe.BRBRecipeBookCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.BrewingStandMenu;

import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BrewingRecipeBookResults extends GenericRecipePage<BrewingStandMenu, BrewingRecipeCollection, BrewableResult> {
    private List<BrewingRecipeCollection> recipeCollection;
    public final List<BrewableAnimatedResultButton> buttons = Lists.newArrayListWithCapacity(20);
    private int currentPage;
    private BrewableAnimatedResultButton hoveredButton;
    BRBRecipeBookCategory category;

    public BrewingRecipeBookResults() {
        super();

        for (int i = 0; i < 20; ++i) {
            this.buttons.add(new BrewableAnimatedResultButton());
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
            BrewableAnimatedResultButton brewableAnimatedResultButton = this.buttons.get(j);
            if (i + j < this.recipeCollection.size()) {
                BrewingRecipeCollection output = this.recipeCollection.get(i + j);
                brewableAnimatedResultButton.showPotionRecipe(output, category, menu);
                brewableAnimatedResultButton.visible = true;
            } else {
                brewableAnimatedResultButton.visible = false;
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
            Iterator<BrewableAnimatedResultButton> var10 = this.buttons.iterator();

            BrewableAnimatedResultButton brewableAnimatedResultButton;
            do {
                if (!var10.hasNext()) {
                    return false;
                }

                brewableAnimatedResultButton = var10.next();
            } while (!brewableAnimatedResultButton.mouseClicked(mouseX, mouseY, button));

            if (button == 0) {
                this.lastClickedRecipeCollection = brewableAnimatedResultButton.getCollection();
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

        for (BrewableAnimatedResultButton brewableAnimatedResultButton : this.buttons) {
            brewableAnimatedResultButton.render(gui, mouseX, mouseY, delta);
            if (brewableAnimatedResultButton.visible && brewableAnimatedResultButton.isHoveredOrFocused()) {
                this.hoveredButton = brewableAnimatedResultButton;
            }
        }

        this.backButton.render(gui, mouseX, mouseY, delta);
        this.forwardButton.render(gui, mouseX, mouseY, delta);
        // this.alternatesWidget.render(matrices, mouseX, mouseY, delta);
    }
}
