package marsh.town.brb.generic;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.interfaces.ISettingsButton;
import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.recipebook.RecipeShownListener;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

public abstract class GenericRecipeBookComponent<M extends AbstractContainerMenu> implements Renderable, NarratableEntry, GuiEventListener, ISettingsButton, RecipeShownListener {
    protected static final Component SEARCH_HINT = RecipeBookComponentAccessor.getSEARCH_HINT();
    protected static final Component ALL_RECIPES_TOOLTIP = RecipeBookComponentAccessor.getALL_RECIPES_TOOLTIP();
    boolean visible;
    protected boolean ignoreTextInput;
    protected Minecraft minecraft;
    protected EditBox searchBox;
    private String lastSearch;
    protected int xOffset;
    protected boolean widthTooNarrow;
    protected int width;
    protected int height;
    protected M menu;
    protected final StackedContents stackedContents = new StackedContents();
    protected StateSwitchingButton filterButton;
    protected ImageButton settingsButton;
//    private int timesInventoryChanged;

    abstract public Component getRecipeFilterName();

    public void init(int width, int height, Minecraft minecraft, boolean widthNarrow, M menu) {
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
        this.menu = menu;
        this.widthTooNarrow = widthNarrow;
        if (this.minecraft.player == null) return;
        this.minecraft.player.containerMenu = menu;

//        this.timesInventoryChanged = minecraft.player.getInventory().getTimesChanged();
    }

    public void initVisuals() {
        if (BetterRecipeBook.config.keepCentered) {
            this.xOffset = this.widthTooNarrow ? 0 : 162;
        } else {
            this.xOffset = this.widthTooNarrow ? 0 : 86;
        }

        int i = (this.width - 147) / 2 - this.xOffset;
        int j = (this.height - 166) / 2;
        this.stackedContents.clear();
        if (this.minecraft.player == null) return;
        this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
        // TODO: menu.fillCraftSlotsStackedContents
//        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        String string = this.searchBox != null ? this.searchBox.getValue() : "";
        Objects.requireNonNull(this.minecraft.font);
        this.searchBox = new EditBox(this.minecraft.font, i + 25, j + 13, 81, this.minecraft.font.lineHeight + 5, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(0xFFFFFF);
        this.searchBox.setValue(string);
        this.searchBox.setHint(SEARCH_HINT);

        this.settingsButton = createSettingsButton(i, j);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        this.ignoreTextInput = false;
        if (!this.isVisible() || this.minecraft.player != null && this.minecraft.player.isSpectator()) {
            return false;
        }
        if (i == 256 && !this.isOffsetNextToMainGUI()) {
            this.setVisible(false);
            return true;
        }
        if (this.searchBox.keyPressed(i, j, k)) {
            this.checkSearchStringUpdate();
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && i != 256) {
            return true;
        }
        if (this.minecraft.options.keyChat.matches(i, j) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocused(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        this.ignoreTextInput = false;
        return GuiEventListener.super.keyReleased(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (this.ignoreTextInput) {
            return false;
        }
        if (!this.isVisible() || this.minecraft.player != null && this.minecraft.player.isSpectator()) {
            return false;
        }
        if (this.searchBox.charTyped(c, i)) {
            this.checkSearchStringUpdate();
            return true;
        }
        return GuiEventListener.super.charTyped(c, i);
    }

    private void checkSearchStringUpdate() {
        String string = this.searchBox.getValue().toLowerCase(Locale.ROOT);
        this.pirateSpeechForThePeople(string);
        if (!string.equals(this.lastSearch)) {
            this.updateCollections(false);
            this.lastSearch = string;
        }
    }

    protected abstract void updateCollections(boolean b);

    private void pirateSpeechForThePeople(String string) {
        if ("excitedze".equals(string)) {
            LanguageManager languageManager = this.minecraft.getLanguageManager();
            String string2 = "en_pt";
            LanguageInfo languageInfo = languageManager.getLanguage("en_pt");
            if (languageInfo == null || languageManager.getSelected().equals("en_pt")) {
                return;
            }
            languageManager.setSelected("en_pt");
            this.minecraft.options.languageCode = "en_pt";
            this.minecraft.reloadResourcePacks();
            this.minecraft.options.save();
        }
    }

    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 86;
    }

    @Override
    @NotNull
    public NarratableEntry.NarrationPriority narrationPriority() {
        return this.isVisible() ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
    }

    protected void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }

    public boolean hasClickedOutside(double d, double e, int i, int j, int k, int l, int m) {
        if (!this.isVisible()) {
            return true;
        }
        boolean bl = d < (double) i || e < (double) j || d >= (double) (i + k) || e >= (double) (j + l);
        boolean bl2 = (double) (i - 147) < d && d < (double) i && (double) j < e && e < (double) (j + l);
//        return bl && !bl2 && !this.selectedTab.isHoveredOrFocused();
        return bl && !bl2;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
//            ArrayList<AbstractWidget> list = Lists.newArrayList();
//            this.recipeBookPage.listButtons(abstractWidget -> {
//                if (abstractWidget.isActive()) {
//                    list.add((AbstractWidget)abstractWidget);
//                }
//            });
//            list.add(this.searchBox);
//            list.add(this.filterButton);
//            list.addAll(this.tabButtons);
//            Screen.NarratableSearchResult narratableSearchResult = Screen.findNarratableWidget(list, null);
//            if (narratableSearchResult != null) {
//                narratableSearchResult.entry.updateNarration(narrationElementOutput.nest());
//            }
    }

    @Override
    public void setFocused(boolean bl) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    protected void updateFilterButtonTooltip() {
        this.filterButton.setTooltip(this.filterButton.isStateTriggered() ? Tooltip.create(this.getRecipeFilterName()) : Tooltip.create(ALL_RECIPES_TOOLTIP));
    }

    public int findLeftEdge(int width, int backgroundWidth) {
        int j;
        if (this.isVisible() && !this.widthTooNarrow) {
            j = 177 + (width - backgroundWidth - 200) / 2;
        } else {
            j = (width - backgroundWidth) / 2;
        }

        return j;
    }

    public void renderSettingsButton(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        ISettingsButton.super.renderSettingsButton(this.settingsButton, gui, mouseX, mouseY, delta);
    }

    public boolean settingsButtonMouseClicked(double mouseX, double mouseY, int button) {
        return ISettingsButton.super.settingsButtonMouseClicked(this.settingsButton, mouseX, mouseY, button);
    }

    public void renderSettingsButtonTooltip(GuiGraphics gui, int mouseX, int mouseY) {
        ISettingsButton.super.renderSettingsButtonTooltip(this.settingsButton, gui, mouseX, mouseY);
    }
}
