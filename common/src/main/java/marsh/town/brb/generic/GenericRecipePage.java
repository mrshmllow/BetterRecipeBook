package marsh.town.brb.generic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class GenericRecipePage<M extends AbstractContainerMenu, C extends GenericRecipeBookCollection<R, M>, R extends GenericRecipe> {
    protected final RegistryAccess registryAccess;
    protected M menu;
    protected Minecraft minecraft;
    protected int parentLeft;
    protected int parentTop;
    protected StateSwitchingButton forwardButton;
    protected StateSwitchingButton backButton;
    protected List<C> recipeCollections = ImmutableList.of();
    protected C lastClickedRecipeCollection;
    protected R lastClickedRecipe;
    protected BRBBookCategories.Category category;
    protected int totalPages;
    protected int currentPage;
    public final List<GenericRecipeButton<C, R, M>> buttons = Lists.newArrayListWithCapacity(20);
    protected GenericRecipeButton<C, R, M> hoveredButton;

    public GenericRecipePage(RegistryAccess registryAccess, Supplier<GenericRecipeButton<C, R, M>> recipeButtonSupplier) {
        this.registryAccess = registryAccess;

        for (int i = 0; i < 20; ++i) {
            this.buttons.add(recipeButtonSupplier.get());
        }
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

        for (int k = 0; k < this.buttons.size(); ++k) {
            this.buttons.get(k).setPosition(parentLeft + 11 + 25 * (k % 5), parentTop + 31 + 25 * (k / 5));
        }
    }

    protected boolean overlayMouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m) {
        return false;
    }

    protected void initOverlay(C recipeCollection, int x, int y, RegistryAccess registryAccess) {
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeCollection = null;

        if (overlayIsVisible() && overlayMouseClicked(mouseX, mouseY, button, j, k, l, m)) {
            return true;
        }

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
            for (GenericRecipeButton<C, R, M> recipeButton : this.buttons) {
                if (!recipeButton.mouseClicked(mouseX, mouseY, button)) continue;
                if (button == 0) {
                    this.lastClickedRecipe = recipeButton.getCurrentDisplayedRecipe();
                    this.lastClickedRecipeCollection = recipeButton.getCollection();
                } else if (button == 1 && !overlayIsVisible() && !recipeButton.isOnlyOption()) {
                    this.initOverlay(recipeButton.getCollection(), this.parentLeft, this.parentTop, registryAccess);
                }
                return true;
            }
        }
        return false;
    }

    public void updateButtonsForPage() {
        int i = 20 * this.currentPage;

        for (int j = 0; j < this.buttons.size(); ++j) {
            var button = this.buttons.get(j);
            if (i + j < this.recipeCollections.size()) {
                C output = this.recipeCollections.get(i + j);
                button.showCollection(output, menu, this.category);
                button.visible = true;
            } else {
                button.visible = false;
            }
        }

        this.updateArrowButtons();
    }

    protected boolean overlayIsVisible() {
        return false;
    }

    protected void render(GuiGraphics gui, int blitX, int blitY, int mouseX, int mouseY, float delta) {
        if (BetterRecipeBook.queuedScroll != 0 && BetterRecipeBook.config.scrolling.enableScrolling) {
            if (totalPages > 1) {
                currentPage += BetterRecipeBook.queuedScroll;
                if (currentPage >= totalPages) {
                    currentPage = BetterRecipeBook.config.scrolling.scrollAround ? currentPage % totalPages : totalPages - 1;
                } else if (currentPage < 0) {
                    // required as % is not modulus, it is remainder. we need to force output positive by((currentPage % totalPages) + totalPages)
                    currentPage = BetterRecipeBook.config.scrolling.scrollAround ? (currentPage % totalPages) + totalPages : 0;
                }

                updateButtonsForPage();
            }
            BetterRecipeBook.queuedScroll = 0;
        }

        if (this.totalPages > 1) {
            String string = this.currentPage + 1 + "/" + this.totalPages;
            int width = this.minecraft.font.width(string);
            gui.drawString(this.minecraft.font, string, blitX - width / 2 + 73, blitY + 141, -1, false);
        }

        this.hoveredButton = null;

        for (var button : this.buttons) {
            button.render(gui, mouseX, mouseY, delta);
            if (button.visible && button.isHoveredOrFocused()) {
                this.hoveredButton = button;
            }
        }

        this.backButton.render(gui, mouseX, mouseY, delta);
        this.forwardButton.render(gui, mouseX, mouseY, delta);
    }

    public void setResults(List<C> recipeCollection, boolean resetCurrentPage, BRBBookCategories.Category category) {
        this.recipeCollections = recipeCollection;
        this.category = category;

        this.totalPages = (int) Math.ceil((double) recipeCollection.size() / 20.0D);
        if (this.totalPages <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0;
        }

        this.updateButtonsForPage();
    }

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

    public void drawTooltip(GuiGraphics gui, int x, int y) {
        if (this.minecraft.screen != null && hoveredButton != null) {
            gui.renderComponentTooltip(Minecraft.getInstance().font, this.hoveredButton.getTooltipText(), x, y);
        }
    }
}
