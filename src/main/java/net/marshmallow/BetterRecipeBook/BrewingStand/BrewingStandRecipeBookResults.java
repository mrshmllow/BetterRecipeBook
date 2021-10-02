package net.marshmallow.BetterRecipeBook.BrewingStand;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.BrewingRecipeBookGroup;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BrewingStandRecipeBookResults {
    private List<BrewingResult> recipeCollection;
    private final List<BrewingAnimatedResultButton> resultButtons = Lists.newArrayListWithCapacity(20);
    private int pageCount;
    private int currentPage;
    private ToggleButtonWidget nextPageButton;
    private ToggleButtonWidget prevPageButton;
    private MinecraftClient client;
    private ClientBrewingStandRecipeBook recipeBook;
    private BrewingAnimatedResultButton hoveredResultButton;
    private BrewingResult lastClickedRecipe;
    @Nullable
    private RecipeResultCollection resultCollection;
    BrewingRecipeBookGroup group;

    public BrewingStandRecipeBookResults() {
        for(int i = 0; i < 20; ++i) {
            this.resultButtons.add(new BrewingAnimatedResultButton());
        }

    }

    public void initialize(MinecraftClient client, int parentLeft, int parentTop) {
        this.client = client;
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
        int i = 20 * this.currentPage;

        for(int j = 0; j < this.resultButtons.size(); ++j) {
            BrewingAnimatedResultButton animatedResultButton = this.resultButtons.get(j);
            if (i + j < this.recipeCollection.size()) {
                BrewingResult output = this.recipeCollection.get(i + j);
                animatedResultButton.showPotionRecipe(output, group);
                animatedResultButton.visible = true;
            } else {
                animatedResultButton.visible = false;
            }
        }

        this.hideShowPageButtons();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight) {
        this.lastClickedRecipe = null;
        this.resultCollection = null;
        if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
            ++this.currentPage;
            this.refreshResultButtons();
            return true;
        } else if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
            --this.currentPage;
            this.refreshResultButtons();
            return true;
        } else {
            Iterator var10 = this.resultButtons.iterator();

            BrewingAnimatedResultButton animatedResultButton;
            do {
                if (!var10.hasNext()) {
                    return false;
                }

                animatedResultButton = (BrewingAnimatedResultButton)var10.next();
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
        this.nextPageButton.visible = this.pageCount > 1 && this.currentPage < this.pageCount - 1;
        this.prevPageButton.visible = this.pageCount > 1 && this.currentPage > 0;
    }

    public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        if (this.pageCount > 1) {
            int var10000 = this.currentPage + 1;
            String string = var10000 + "/" + this.pageCount;
            int i = this.client.textRenderer.getWidth(string);
            this.client.textRenderer.draw(matrices, (String)string, (float)(x - i / 2 + 73), (float)(y + 141), -1);
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
