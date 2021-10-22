package net.marshmallow.BetterRecipeBook.BrewingStand;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.BrewingStandScreenHandler;

import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BrewingStandRecipeBookResults {
    private List<BrewingResult> recipeCollection;
    public final List<BrewingAnimatedResultButton> resultButtons = Lists.newArrayListWithCapacity(20);
    private int pageCount;
    private int currentPage;
    private ToggleButtonWidget nextPageButton;
    private ToggleButtonWidget prevPageButton;
    private MinecraftClient client;
    private BrewingAnimatedResultButton hoveredResultButton;
    private BrewingResult lastClickedRecipe;
    BrewingRecipeBookGroup group;
    private BrewingStandScreenHandler brewingStandScreenHandler;

    public BrewingStandRecipeBookResults() {
        for(int i = 0; i < 20; ++i) {
            this.resultButtons.add(new BrewingAnimatedResultButton());
        }

    }

    public void initialize(MinecraftClient client, int parentLeft, int parentTop, BrewingStandScreenHandler brewingStandScreenHandler) {
        this.client = client;
        this.brewingStandScreenHandler = brewingStandScreenHandler;
        // this.recipeBook = client.player.getRecipeBook();

        for(int i = 0; i < this.resultButtons.size(); ++i) {
            this.resultButtons.get(i).setPos(parentLeft + 11 + 25 * (i % 5), parentTop + 31 + 25 * (i / 5));
        }

        this.nextPageButton = new ToggleButtonWidget(parentLeft + 93, parentTop + 137, 12, 17, false);
        this.nextPageButton.setTextureUV(1, 208, 13, 18, BrewingStandRecipeBookWidget.TEXTURE);
        this.prevPageButton = new ToggleButtonWidget(parentLeft + 38, parentTop + 137, 12, 17, true);
        this.prevPageButton.setTextureUV(1, 208, 13, 18, BrewingStandRecipeBookWidget.TEXTURE);
    }

    public void setResults(List<BrewingResult> recipeCollection, boolean resetCurrentPage, BrewingRecipeBookGroup group) {
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

        for(int j = 0; j < this.resultButtons.size(); ++j) {
            BrewingAnimatedResultButton animatedResultButton = this.resultButtons.get(j);
            if (i + j < this.recipeCollection.size()) {
                BrewingResult output = this.recipeCollection.get(i + j);
                animatedResultButton.showPotionRecipe(output, group, brewingStandScreenHandler);
                animatedResultButton.visible = true;
            } else {
                animatedResultButton.visible = false;
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

        this.lastClickedRecipe = null;
        if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
            ++this.currentPage;
            this.refreshResultButtons();
            return true;
        } else if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
            --this.currentPage;
            this.refreshResultButtons();
            return true;
        } else {
            Iterator<BrewingAnimatedResultButton> var10 = this.resultButtons.iterator();

            BrewingAnimatedResultButton animatedResultButton;
            do {
                if (!var10.hasNext()) {
                    return false;
                }

                animatedResultButton = var10.next();
            } while(!animatedResultButton.mouseClicked(mouseX, mouseY, button));

            if (button == 0) {
                this.lastClickedRecipe = animatedResultButton.getRecipe();
            }

            return true;
        }
    }

    public void drawTooltip(MatrixStack matrices, int x, int y) {
        if (this.client.currentScreen != null && hoveredResultButton != null) {
            this.client.currentScreen.renderTooltip(matrices, this.hoveredResultButton.getTooltip(), x, y);
        }
    }

    public BrewingResult getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    private void hideShowPageButtons() {
        if (BetterRecipeBook.config.scrolling.scrollAround && !(pageCount < 1)) {
            nextPageButton.visible = true;
            prevPageButton.visible = true;
        } else {
            nextPageButton.visible = pageCount > 1 && currentPage < pageCount - 1;
            prevPageButton.visible = pageCount > 1 && currentPage > 0;
        }
    }

    public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
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
            int var10000 = this.currentPage + 1;
            String string = var10000 + "/" + this.pageCount;
            int i = this.client.textRenderer.getWidth(string);
            this.client.textRenderer.draw(matrices, string, (float)(x - i / 2 + 73), (float)(y + 141), -1);
        }

        this.hoveredResultButton = null;

        for (BrewingAnimatedResultButton animatedResultButton : this.resultButtons) {
            animatedResultButton.render(matrices, mouseX, mouseY, delta);
            if (animatedResultButton.visible && animatedResultButton.isHovered()) {
                this.hoveredResultButton = animatedResultButton;
            }
        }

        this.prevPageButton.render(matrices, mouseX, mouseY, delta);
        this.nextPageButton.render(matrices, mouseX, mouseY, delta);
        // this.alternatesWidget.render(matrices, mouseX, mouseY, delta);
    }
}
