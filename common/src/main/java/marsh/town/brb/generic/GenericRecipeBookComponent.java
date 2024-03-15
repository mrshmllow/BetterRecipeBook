package marsh.town.brb.generic;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.api.BRBBookSettings;
import marsh.town.brb.interfaces.IPinningComponent;
import marsh.town.brb.interfaces.ISettingsButton;
import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
import marsh.town.brb.util.BRBHelper;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.recipebook.RecipeShownListener;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public abstract class GenericRecipeBookComponent<M extends AbstractContainerMenu, C extends GenericRecipeBookCollection<R, M>, R extends GenericRecipe> implements Renderable, NarratableEntry, GuiEventListener, ISettingsButton, RecipeShownListener, IPinningComponent<C> {
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
    public GenericRecipePage<M, C, R> recipesPage;
    protected final List<BRBGroupButtonWidget> tabButtons = Lists.newArrayList();
    @Nullable
    public BRBGroupButtonWidget selectedTab;
    protected GenericClientRecipeBook book;
    protected RecipeManager recipeManager;

    private boolean doubleRefresh = true;
    protected RegistryAccess registryAccess;
    @Nullable
    public GenericGhostRecipe<R> ghostRecipe;

//    private int timesInventoryChanged;

    protected GenericRecipeBookComponent() {
    }

    abstract public Component getRecipeFilterName();

    abstract public BRBHelper.Book getRecipeBookType();

    public void init(int parentWidth, int parentHeight, Minecraft client, boolean narrow, M menu, RegistryAccess registryAccess) {
        this.init(parentWidth, parentHeight, client, narrow, menu, null, registryAccess);
    }

    public void init(int width, int height, Minecraft minecraft, boolean widthNarrow, M menu, @Nullable Consumer<ItemStack> onGhostRecipeUpdate, RegistryAccess registryAccess) {
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
        this.menu = menu;
        this.widthTooNarrow = widthNarrow;
        if (this.minecraft.player == null) return;
        this.minecraft.player.containerMenu = menu;

        this.setVisible(BRBBookSettings.isOpen(this.getRecipeBookType()));

        this.book = new GenericClientRecipeBook();
        this.registryAccess = registryAccess;

        this.ghostRecipe = new GenericGhostRecipe<>(onGhostRecipeUpdate, registryAccess);

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
        this.recipesPage.initialize(this.minecraft, i, j, menu, xOffset);
        this.tabButtons.clear();
        this.filterButton = new StateSwitchingButton(i + 110, j + 12, 26, 16, BRBBookSettings.isFiltering(this.getRecipeBookType()));
        this.updateFilterButtonTooltip();
        this.filterButton.initTextureValues(BRBTextures.RECIPE_BOOK_FILTER_BUTTON_SPRITES);

        List<BRBBookCategories.Category> categories = BRBBookCategories.getCategories(this.getRecipeBookType());

        if (categories == null) throw new NullPointerException("Book category not registered");

        for (BRBBookCategories.Category category : categories) {
            this.tabButtons.add(new BRBGroupButtonWidget(category));
        }

        if (this.selectedTab != null) {
            this.selectedTab = this.tabButtons.stream().filter((button) -> button.getCategory().equals(this.selectedTab.getCategory())).findFirst().orElse(null);
        }

        if (this.selectedTab == null) {
            this.selectedTab = this.tabButtons.get(0);
        }

        this.selectedTab.setStateTriggered(true);
        this.updateCollections(false);
        this.refreshTabButtons();
    }

    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) return;

        if (this.doubleRefresh) {
            // Minecraft doesn't populate the inventory on initialization so this is the only solution I have
            updateCollections(true);
            this.doubleRefresh = false;
        }

        gui.pose().pushPose();
        gui.pose().translate(0.0f, 0.0f, 100.0f);

        // blit recipe book background texture
        int blitX = (this.width - 147) / 2 - this.xOffset;
        int blitY = (this.height - 166) / 2;
        gui.blit(BRBTextures.RECIPE_BOOK_BACKGROUND_TEXTURE, blitX, blitY, 1, 1, 147, 166);

        // render search box
        this.searchBox.render(gui, mouseX, mouseY, delta);

        // render tab buttons
        for (BRBGroupButtonWidget widget : this.tabButtons) {
            widget.render(gui, mouseX, mouseY, delta);
        }

        this.filterButton.render(gui, mouseX, mouseY, delta);

        ISettingsButton.super.renderSettingsButton(this.settingsButton, gui, mouseX, mouseY, delta);

        // render the recipe book page contents
        this.recipesPage.render(gui, blitX, blitY, mouseX, mouseY, delta);

        gui.pose().popPose();
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

        if (BetterRecipeBook.PIN_MAPPING.matches(i, j) && BetterRecipeBook.config.enablePinning) {
            for (GenericRecipeButton<C, R, M> resultButton : this.recipesPage.buttons) {
                if (resultButton.isHoveredOrFocused()) {
                    BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavourite(resultButton.getCollection());
                    this.updateCollections(false);
                    return true;
                }
            }
        }

        return false;
    }

    public abstract void handlePlaceRecipe();

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

    protected void updateCollections(boolean b) {
        if (this.selectedTab == null) return;
        if (this.searchBox == null) return;

        // Create a copy to not mess with the original list
        List<C> results = new ArrayList<>(this.getCollectionsForCategory());

        String string = this.searchBox.getValue();
        if (!string.isEmpty()) {
            results.removeIf(collection -> !collection.getFirst().getSearchString(selectedTab.getCategory()).toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)));
        }

        if (BRBBookSettings.isFiltering(this.getRecipeBookType())) {
            results.removeIf((result) -> !result.atleastOneCraftable(this.menu.slots));
        }

        this.betterRecipeBook$sortByPinsInPlace(results);

        this.recipesPage.setResults(results, b, selectedTab.getCategory());
    }

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
        BRBBookSettings.setOpen(getRecipeBookType(), visible);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isVisible()) return false;

        if (this.recipesPage.mouseClicked(mouseX, mouseY, button, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
            this.handlePlaceRecipe();
            return true;
        }

        if (this.searchBox.mouseClicked(mouseX, mouseY, button)) {
            searchBox.setFocused(true);
            ignoreTextInput = true;
            return true;
        }

        searchBox.setFocused(false);
        ignoreTextInput = false;

        if (this.filterButton.mouseClicked(mouseX, mouseY, button)) {
            boolean bl = this.toggleFiltering();
            this.filterButton.setStateTriggered(bl);
            this.updateFilterButtonTooltip();
//                    this.sendUpdateSettings();
            this.updateCollections(false);
            return true;
        }

        if (ISettingsButton.super.settingsButtonMouseClicked(this.settingsButton, mouseX, mouseY, button)) {
            return true;
        }

        Iterator<BRBGroupButtonWidget> tabButtonsIter = this.tabButtons.iterator();

        BRBGroupButtonWidget widget;
        if (!tabButtonsIter.hasNext()) {
            return false;
        }

        widget = tabButtonsIter.next();
        while (!widget.mouseClicked(mouseX, mouseY, button)) {
            if (!tabButtonsIter.hasNext()) {
                return false;
            }

            widget = tabButtonsIter.next();
        }

        if (this.selectedTab != widget) {
            if (this.selectedTab != null) {
                this.selectedTab.setStateTriggered(false);
            }

            this.selectedTab = widget;
            this.selectedTab.setStateTriggered(true);
            this.updateCollections(true);
        }

        return false;
    }

    protected boolean toggleFiltering() {
        boolean bl = !BRBBookSettings.isFiltering(this.getRecipeBookType());
        BRBBookSettings.setFiltering(this.getRecipeBookType(), bl);

        return bl;
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

    public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
        if (!this.isVisible()) {
            return;
        }

        if (!this.recipesPage.overlayIsVisible()) {
            this.recipesPage.drawTooltip(gui, mouseX, mouseY);

            ISettingsButton.super.renderSettingsButtonTooltip(this.settingsButton, gui, mouseX, mouseY);
        }

        this.ghostRecipe.drawTooltip(gui, x, y, mouseX, mouseY);
    }

    protected void refreshTabButtons() {
        int i = (this.width - 147) / 2 - this.xOffset - 30;
        int j = (this.height - 166) / 2 + 3;
        int l = 0;

        for (BRBGroupButtonWidget button : this.tabButtons) {
            BRBBookCategories.Category category = button.getCategory();
            if (category.getType() == BRBBookCategories.Category.Type.SEARCH) {
                button.visible = true;
            }
            button.setPosition(i, j + 27 * l++);
        }
    }

    public void renderGhostRecipe(GuiGraphics guiGraphics, int x, int y, boolean bl, float delta) {
        if (selectedTab == null || ghostRecipe == null) return;

        this.ghostRecipe.render(guiGraphics, this.minecraft, x, y, bl, delta, selectedTab.getCategory());
    }

    protected abstract List<C> getCollectionsForCategory();

    @Override
    public void recipesShown(List<RecipeHolder<?>> list) {

    }
}
