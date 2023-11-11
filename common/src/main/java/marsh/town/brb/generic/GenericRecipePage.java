package marsh.town.brb.generic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.recipe.BRBRecipeBookCategory;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GenericRecipePage<M extends AbstractContainerMenu, C extends GenericRecipeBookCollection<R, M>, R extends GenericRecipe, B> {
    protected M menu;
    protected Minecraft minecraft;
    protected int parentLeft;
    protected int parentTop;
    protected StateSwitchingButton forwardButton;
    protected StateSwitchingButton backButton;
    protected List<C> recipeCollections = ImmutableList.of();
    protected C lastClickedRecipeCollection;
    protected R lastClickedRecipe;
    protected BRBRecipeBookCategory category;
    protected int totalPages;
    protected int currentPage;
    public final List<B> buttons = Lists.newArrayListWithCapacity(20);
    protected B hoveredButton;

    protected GenericRecipePage() {

    }

    protected void initialize(Minecraft client, int parentLeft, int parentTop, M menu, int leftOffset) {
        this.minecraft = client;
        this.menu = menu;

        this.parentLeft = parentLeft;
        this.parentTop = parentTop;

        this.forwardButton = new StateSwitchingButton(parentLeft + 93, parentTop + 137, 12, 17, false);
        this.forwardButton.initTextureValues(BRBTextures.RECIPE_BOOK_PAGE_FORWARD_SPRITES);
        this.backButton = new StateSwitchingButton(parentLeft + 38, parentTop + 137, 12, 17, true);
        this.backButton.initTextureValues(BRBTextures.RECIPE_BOOK_PAGE_BACKWARD_SPRITES);
    }

    protected abstract boolean mouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m);

    protected abstract void drawTooltip(GuiGraphics gui, int mouseX, int mouseY);

    protected abstract boolean overlayIsVisible();

    protected abstract boolean isFilteringCraftable();

    protected void render(GuiGraphics gui, int blitX, int blitY, int mouseX, int mouseY, float delta) {
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
            gui.drawString(this.minecraft.font, string, blitX - width / 2 + 73, blitY + 141, -1, false);
        }
    }

    public void setResults(List<C> recipeCollection, boolean resetCurrentPage, BRBRecipeBookCategory category) {
        this.recipeCollections = recipeCollection;
        this.category = category;

        this.totalPages = (int) Math.ceil((double) recipeCollection.size() / 20.0D);
        if (this.totalPages <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0;
        }

        this.updateButtonsForPage();
    }

    protected abstract void updateButtonsForPage();

    @Nullable
    public R getCurrentClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public C getLastClickedRecipeCollection() {
        return this.lastClickedRecipeCollection;
    }

    protected void updateArrowButtons() {
        if (BetterRecipeBook.config.scrolling.scrollAround && totalPages > 1) {
            forwardButton.visible = true;
            backButton.visible = true;
        } else {
            forwardButton.visible = totalPages > 1 && currentPage < totalPages - 1;
            backButton.visible = totalPages > 1 && currentPage > 0;
        }
    }
}
