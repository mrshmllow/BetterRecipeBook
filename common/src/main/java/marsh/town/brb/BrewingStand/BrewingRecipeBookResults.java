package marsh.town.brb.BrewingStand;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.world.inventory.BrewingStandMenu;

import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BrewingRecipeBookResults {
    private List<BrewableResult> recipeCollection;
    public final List<BrewableAnimatedResultButton> buttons = Lists.newArrayListWithCapacity(20);
    private int pageCount;
    private int currentPage;
    private StateSwitchingButton nextPageButton;
    private StateSwitchingButton prevPageButton;
    private Minecraft minecraft;
    private BrewableAnimatedResultButton hoveredButton;
    private BrewableResult currentClickedRecipe;
    private BrewableResult lastClickedRecipe;
    BrewingRecipeBookGroup group;
    private BrewingStandMenu brewingStandScreenHandler;

    public BrewingRecipeBookResults() {
        for(int i = 0; i < 20; ++i) {
            this.buttons.add(new BrewableAnimatedResultButton());
        }

    }

    public void initialize(Minecraft client, int parentLeft, int parentTop, BrewingStandMenu brewingStandScreenHandler) {
        this.minecraft = client;
        this.brewingStandScreenHandler = brewingStandScreenHandler;
        // this.recipeBook = client.player.getRecipeBook();

        for (int k = 0; k < this.buttons.size(); ++k) {
            this.buttons.get(k).setPosition(parentLeft + 11 + 25 * (k % 5), parentTop + 31 + 25 * (k / 5));
        }

        this.nextPageButton = new StateSwitchingButton(parentLeft + 93, parentTop + 137, 12, 17, false);
        this.nextPageButton.initTextureValues(1, 208, 13, 18, BrewingRecipeBookComponent.RECIPE_BOOK_LOCATION);
        this.prevPageButton = new StateSwitchingButton(parentLeft + 38, parentTop + 137, 12, 17, true);
        this.prevPageButton.initTextureValues(1, 208, 13, 18, BrewingRecipeBookComponent.RECIPE_BOOK_LOCATION);
    }

    public void setResults(List<BrewableResult> recipeCollection, boolean resetCurrentPage, BrewingRecipeBookGroup group) {
        this.recipeCollection = recipeCollection;
        this.group = group;

        this.pageCount = (int)Math.ceil((double)recipeCollection.size() / 20.0D);
        if (this.pageCount <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0;
        }

        this.refreshResultButtons();
    }

    private void refreshResultButtons() {
        if (pageCount == 0 && currentPage == -1) {
            currentPage = 0;
            return;
        }

        int i = 20 * this.currentPage;

        for(int j = 0; j < this.buttons.size(); ++j) {
            BrewableAnimatedResultButton brewableAnimatedResultButton = this.buttons.get(j);
            if (i + j < this.recipeCollection.size()) {
                BrewableResult output = this.recipeCollection.get(i + j);
                brewableAnimatedResultButton.showPotionRecipe(output, group, brewingStandScreenHandler);
                brewableAnimatedResultButton.visible = true;
            } else {
                brewableAnimatedResultButton.visible = false;
            }
        }

        this.hideShowPageButtons();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (BetterRecipeBook.config.scrolling.enableScrolling) {
            if (nextPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (currentPage >= pageCount - 1) {
                    currentPage = -1;
                }
            } else if (prevPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (currentPage <= 0) {
                    currentPage = pageCount;
                }
            }
        }

        this.currentClickedRecipe = null;
        if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
            ++this.currentPage;
            this.refreshResultButtons();
            return true;
        } else if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
            --this.currentPage;
            this.refreshResultButtons();
            return true;
        } else {
            Iterator<BrewableAnimatedResultButton> var10 = this.buttons.iterator();

            BrewableAnimatedResultButton brewableAnimatedResultButton;
            do {
                if (!var10.hasNext()) {
                    return false;
                }

                brewableAnimatedResultButton = var10.next();
            } while(!brewableAnimatedResultButton.mouseClicked(mouseX, mouseY, button));

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

    public BrewableResult getCurrentClickedRecipe() {
        return this.currentClickedRecipe;
    }

    public BrewableResult getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    private void hideShowPageButtons() {
        if (BetterRecipeBook.config.scrolling.scrollAround && BetterRecipeBook.config.scrolling.enableScrolling && !(pageCount < 1)) {
            nextPageButton.visible = true;
            prevPageButton.visible = true;
        } else {
            nextPageButton.visible = pageCount > 1 && currentPage < pageCount - 1;
            prevPageButton.visible = pageCount > 1 && currentPage > 0;
        }
    }

    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float delta) {
        if (BetterRecipeBook.queuedScroll != 0 && BetterRecipeBook.config.scrolling.enableScrolling) {
            int queuedPage = BetterRecipeBook.queuedScroll + currentPage;

            if (queuedPage <= pageCount - 1 && queuedPage >= 0) {
                currentPage += BetterRecipeBook.queuedScroll;
            } else if (BetterRecipeBook.config.scrolling.scrollAround) {
                if (queuedPage < 0) {
                    currentPage = pageCount - 1;
                } else if (queuedPage > pageCount - 1) {
                    currentPage = 0;
                }
            }

            refreshResultButtons();
            BetterRecipeBook.queuedScroll = 0;
        }


        if (this.pageCount > 1) {
            String string = this.currentPage + 1 + "/" + this.pageCount;
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

        this.prevPageButton.render(gui, mouseX, mouseY, delta);
        this.nextPageButton.render(gui, mouseX, mouseY, delta);
        // this.alternatesWidget.render(matrices, mouseX, mouseY, delta);
    }
}
