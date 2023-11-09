package marsh.town.brb.smithingtable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.SmithingMenu;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmithingRecipeBookPage {
    public final List<SmithableRecipeButton> buttons = Lists.newArrayListWithCapacity(20);
    private int totalPages;
    private Minecraft minecraft;
    private SmithingMenu smithingMenuHandler;
    private StateSwitchingButton forwardButton;
    private StateSwitchingButton backButton;
    private List<SmithingRecipeCollection> recipeCollections = ImmutableList.of();
    BRBRecipeBookCategories group;
    private int currentPage;
    private SmithableRecipeButton hoveredButton;
    private BRBSmithingRecipe lastClickedRecipe;
    public final SmithingOverlayRecipeComponent overlay = new SmithingOverlayRecipeComponent();
    private SmithingRecipeCollection lastClickedRecipeCollection;
    private int leftOffset;
    private int parentLeft;
    private int parentTop;
    private RegistryAccess registryAccess;

    public SmithingRecipeBookPage(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
        for (int i = 0; i < 20; ++i) {
            this.buttons.add(new SmithableRecipeButton(registryAccess));
        }
    }

    public void initialize(Minecraft client, int parentLeft, int parentTop, SmithingMenu smithingMenuHandler, int leftOffset) {
        this.minecraft = client;
        this.smithingMenuHandler = smithingMenuHandler;
        // this.recipeBook = client.player.getRecipeBook();
        this.parentLeft = parentLeft;
        this.parentTop = parentTop;

        for (int k = 0; k < this.buttons.size(); ++k) {
            this.buttons.get(k).setPosition(parentLeft + 11 + 25 * (k % 5), parentTop + 31 + 25 * (k / 5));
        }

        this.forwardButton = new StateSwitchingButton(parentLeft + 93, parentTop + 137, 12, 17, false);
        this.forwardButton.initTextureValues(BRBTextures.RECIPE_BOOK_PAGE_FORWARD_SPRITES);
        this.backButton = new StateSwitchingButton(parentLeft + 38, parentTop + 137, 12, 17, true);
        this.backButton.initTextureValues(BRBTextures.RECIPE_BOOK_PAGE_BACKWARD_SPRITES);
        this.leftOffset = leftOffset;
    }

    public void setResults(List<SmithingRecipeCollection> recipeCollection, boolean resetCurrentPage, BRBRecipeBookCategories group) {
        this.recipeCollections = recipeCollection;
        this.group = group;

        this.totalPages = (int) Math.ceil((double) recipeCollection.size() / 20.0D);
        if (this.totalPages <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0;
        }

        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int i = 20 * this.currentPage;

        for (int j = 0; j < this.buttons.size(); ++j) {
            SmithableRecipeButton smithableRecipeButton = this.buttons.get(j);
            if (i + j < this.recipeCollections.size()) {
                SmithingRecipeCollection output = this.recipeCollections.get(i + j);
                smithableRecipeButton.showSmithableRecipe(output, smithingMenuHandler);
                smithableRecipeButton.visible = true;
            } else {
                smithableRecipeButton.visible = false;
            }
        }

        this.updateArrowButtons();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeCollection = null;
        if (this.overlay.isVisible()) {
            if (this.overlay.mouseClicked(mouseX, mouseY, button)) {
                this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
                this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
            } else {
                this.overlay.setVisible(false);
            }
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
            for (SmithableRecipeButton recipeButton : this.buttons) {
                if (!recipeButton.mouseClicked(mouseX, mouseY, button)) continue;
                if (button == 0) {
                    this.lastClickedRecipe = recipeButton.getCurrentArmour();
                    this.lastClickedRecipeCollection = recipeButton.getCollection();
                } else if (button == 1 && !this.overlay.isVisible() && !recipeButton.isOnlyOption()) {
                    this.overlay.init(recipeButton.getCollection(), this.parentLeft, this.parentTop, registryAccess);
                }
                return true;
            }
        }
        return false;
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

        for (SmithableRecipeButton smithableRecipeButton : this.buttons) {
            smithableRecipeButton.render(gui, mouseX, mouseY, delta);
            if (smithableRecipeButton.visible && smithableRecipeButton.isHoveredOrFocused()) {
                this.hoveredButton = smithableRecipeButton;
            }
        }

        this.backButton.render(gui, mouseX, mouseY, delta);
        this.forwardButton.render(gui, mouseX, mouseY, delta);
        this.overlay.render(gui, mouseX, mouseY, delta);
    }

    public void drawTooltip(GuiGraphics gui, int x, int y) {
        if (this.minecraft.screen != null && hoveredButton != null) {
            gui.renderComponentTooltip(Minecraft.getInstance().font, this.hoveredButton.getTooltipText(), x, y);
        }
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

    @Nullable
    public BRBSmithingRecipe getCurrentClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public SmithingRecipeCollection getLastClickedRecipeCollection() {
        return this.lastClickedRecipeCollection;
    }
}
