package marsh.town.brb.smithingtable;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.GenericRecipePage;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.SmithingMenu;

import java.util.List;

public class SmithingRecipeBookPage extends GenericRecipePage<SmithingMenu, SmithingRecipeCollection, BRBSmithingRecipe> {
    public final List<SmithableRecipeButton> buttons = Lists.newArrayListWithCapacity(20);
    private SmithableRecipeButton hoveredButton;
    public final SmithingOverlayRecipeComponent overlay = new SmithingOverlayRecipeComponent();
    private int leftOffset;
    private RegistryAccess registryAccess;

    public SmithingRecipeBookPage(RegistryAccess registryAccess) {
        super();

        this.registryAccess = registryAccess;
        for (int i = 0; i < 20; ++i) {
            this.buttons.add(new SmithableRecipeButton(registryAccess));
        }
    }

    @Override
    public void initialize(Minecraft client, int parentLeft, int parentTop, SmithingMenu menu, int leftOffset) {
        super.initialize(client, parentLeft, parentTop, this.menu, leftOffset);

        // this.recipeBook = client.player.getRecipeBook();

        for (int k = 0; k < this.buttons.size(); ++k) {
            this.buttons.get(k).setPosition(parentLeft + 11 + 25 * (k % 5), parentTop + 31 + 25 * (k / 5));
        }
        this.leftOffset = leftOffset;
    }

    @Override
    public void updateButtonsForPage() {
        int i = 20 * this.currentPage;

        for (int j = 0; j < this.buttons.size(); ++j) {
            SmithableRecipeButton smithableRecipeButton = this.buttons.get(j);
            if (i + j < this.recipeCollections.size()) {
                SmithingRecipeCollection output = this.recipeCollections.get(i + j);
                smithableRecipeButton.showSmithableRecipe(output, menu);
                smithableRecipeButton.visible = true;
            } else {
                smithableRecipeButton.visible = false;
            }
        }

        this.updateArrowButtons();
    }

    @Override
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

    @Override
    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(gui, x, y, mouseX, mouseY, delta);

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

    @Override
    public void drawTooltip(GuiGraphics gui, int x, int y) {
        if (this.minecraft.screen != null && hoveredButton != null) {
            gui.renderComponentTooltip(Minecraft.getInstance().font, this.hoveredButton.getTooltipText(), x, y);
        }
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
