package marsh.town.brb.brewingstand;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.GenericRecipePage;
import marsh.town.brb.recipe.BRBRecipeBookCategories;
import marsh.town.brb.util.BRBTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.world.inventory.BrewingStandMenu;

import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BrewingRecipeBookResults implements GenericRecipePage<BrewingStandMenu> {
    private List<BrewableResult> recipeCollection;
    public final List<BrewableAnimatedResultButton> buttons = Lists.newArrayListWithCapacity(20);
    private int totalPages;
    private int currentPage;
    private StateSwitchingButton forwardButton;
    private StateSwitchingButton backButton;
    private Minecraft minecraft;
    private BrewableAnimatedResultButton hoveredButton;
    private BrewableResult currentClickedRecipe;
    private BrewableResult lastClickedRecipe;
    BRBRecipeBookCategories categories;
    private BrewingStandMenu brewingStandScreenHandler;

    public BrewingRecipeBookResults() {
        for (int i = 0; i < 20; ++i) {
            this.buttons.add(new BrewableAnimatedResultButton());
        }

    }

    @Override
    public void initialize(Minecraft client, int parentLeft, int parentTop, BrewingStandMenu brewingStandScreenHandler, int leftOffset) {
        this.minecraft = client;
        this.brewingStandScreenHandler = brewingStandScreenHandler;
        // this.recipeBook = client.player.getRecipeBook();

        for (int k = 0; k < this.buttons.size(); ++k) {
            this.buttons.get(k).setPosition(parentLeft + 11 + 25 * (k % 5), parentTop + 31 + 25 * (k / 5));
        }

        this.forwardButton = new StateSwitchingButton(parentLeft + 93, parentTop + 137, 12, 17, false);
        this.forwardButton.initTextureValues(BRBTextures.RECIPE_BOOK_PAGE_FORWARD_SPRITES);
        this.backButton = new StateSwitchingButton(parentLeft + 38, parentTop + 137, 12, 17, true);
        this.backButton.initTextureValues(BRBTextures.RECIPE_BOOK_PAGE_BACKWARD_SPRITES);
    }

    public void setResults(List<BrewableResult> recipeCollection, boolean resetCurrentPage, BRBRecipeBookCategories categories) {
        this.recipeCollection = recipeCollection;
        this.categories = categories;

        this.totalPages = (int) Math.ceil((double) recipeCollection.size() / 20.0D);
        if (this.totalPages <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0;
        }

        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int i = 20 * this.currentPage;

        for (int j = 0; j < this.buttons.size(); ++j) {
            BrewableAnimatedResultButton brewableAnimatedResultButton = this.buttons.get(j);
            if (i + j < this.recipeCollection.size()) {
                BrewableResult output = this.recipeCollection.get(i + j);
                brewableAnimatedResultButton.showPotionRecipe(output, categories, brewingStandScreenHandler);
                brewableAnimatedResultButton.visible = true;
            } else {
                brewableAnimatedResultButton.visible = false;
            }
        }

        this.updateArrowButtons();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m) {
        this.currentClickedRecipe = null;
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
                this.lastClickedRecipe = this.currentClickedRecipe = brewableAnimatedResultButton.getRecipe();
            }

            return true;
        }
    }

    public void drawTooltip(GuiGraphics gui, int x, int y) {
        if (this.minecraft.screen != null && hoveredButton != null) {
            gui.renderComponentTooltip(Minecraft.getInstance().font, this.hoveredButton.getTooltipText(), x, y);
        }
    }

    @Override
    public boolean overlayIsVisible() {
        return false;
    }

    public BrewableResult getCurrentClickedRecipe() {
        return this.currentClickedRecipe;
    }

    public BrewableResult getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    private void updateArrowButtons() {
        if (BetterRecipeBook.config.scrolling.scrollAround && totalPages > 1) {
            forwardButton.visible = true;
            backButton.visible = true;
        } else {
            forwardButton.visible = totalPages > 1 && currentPage < totalPages - 1;
            backButton.visible = totalPages > 1 && currentPage > 0;
        }
    }

    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float delta) {
        if (BetterRecipeBook.queuedScroll != 0 && BetterRecipeBook.config.scrolling.enableScrolling) {
            currentPage += BetterRecipeBook.queuedScroll;
            BetterRecipeBook.queuedScroll = 0;

            if (currentPage >= totalPages) {
                currentPage = BetterRecipeBook.config.scrolling.scrollAround ? currentPage % totalPages : totalPages - 1;
            } else if (currentPage < 0) {
                // required as % is not modulus, it is remainder. we need to force output positive by((currentPage % totalPages) + totalPages)
                currentPage = BetterRecipeBook.config.scrolling.scrollAround ? (currentPage % totalPages) + totalPages : 0;
            }

            updateButtonsForPage();
        }


        if (this.totalPages > 1) {
            String string = this.currentPage + 1 + "/" + this.totalPages;
            int width = this.minecraft.font.width(string);
            gui.drawString(this.minecraft.font, string, x - width / 2 + 73, y + 141, -1, false);
        }

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
