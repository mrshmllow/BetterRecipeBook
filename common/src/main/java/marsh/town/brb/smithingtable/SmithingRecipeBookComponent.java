package marsh.town.brb.smithingtable;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.config.Config;
import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
import marsh.town.brb.smithingtable.recipe.BRBSmithingRecipe;
import marsh.town.brb.util.BRBTextures;
import marsh.town.brb.util.ClientInventoryUtil;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Consumer;

public class SmithingRecipeBookComponent extends RecipeBookComponent {
    private boolean open;
    private Minecraft minecraft;
    private int width;
    private int height;
    private SmithingMenu smithingScreenHandler;
    private boolean narrow;
    private SmithingClientRecipeBook recipeBook;
    private int leftOffset;
    private final StackedContents recipeFinder = new StackedContents();
    private EditBox searchBox;
    private static final MutableComponent SEARCH_HINT;
    private static final MutableComponent ONLY_CRAFTABLES_TOOLTIP;
    private static final MutableComponent ALL_RECIPES_TOOLTIP;
    private static final MutableComponent OPEN_SETTINGS_TOOLTIP;
    public SmithingRecipeBookPage recipesPage;
    private final List<SmithingRecipeGroupButtonWidget> tabButtons = Lists.newArrayList();
    @Nullable
    public SmithingRecipeGroupButtonWidget currentTab;
    protected StateSwitchingButton toggleSmithableButton;
    private ImageButton settingsButton;
    boolean doubleRefresh = true;
    private boolean searching;
    private String searchText;
    @Nullable
    public SmithingGhostRecipe ghostRecipe;
    private RegistryAccess registryAccess;
    private RecipeManager recipeManager;

    public void initialize(int width, int height, Minecraft minecraft, boolean widthNarrow, SmithingMenu menu, Consumer<SmithingGhostRecipe> onGhostRecipeUpdate, RegistryAccess registryAccess, RecipeManager recipeManager) {
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
        this.smithingScreenHandler = menu;
        this.narrow = widthNarrow;
        this.minecraft.player.containerMenu = smithingScreenHandler;
        this.recipeBook = new SmithingClientRecipeBook();
        this.open = BetterRecipeBook.rememberedSmithingOpen;
        this.registryAccess = registryAccess;
        this.recipeManager = recipeManager;
        // this.cachedInvChangeCount = client.player.getInventory().getChangeCount();
        this.ghostRecipe = new SmithingGhostRecipe(onGhostRecipeUpdate, registryAccess);
        this.recipesPage = new SmithingRecipeBookPage(registryAccess);

        if (BetterRecipeBook.config.keepCentered) {
            this.leftOffset = this.narrow ? 0 : 162;
        } else {
            this.leftOffset = this.narrow ? 0 : 86;
        }

        this.reset();

        // still required?
        //client.keyboardHandler.setSendRepeatsToGui(true);
    }

    public void reset() {
        if (BetterRecipeBook.config.keepCentered) {
            this.leftOffset = this.narrow ? 0 : 162;
        } else {
            this.leftOffset = this.narrow ? 0 : 86;
        }

        int i = (this.width - 147) / 2 - this.leftOffset;
        int j = (this.height - 166) / 2;
        this.recipeFinder.clear();
        assert this.minecraft.player != null;
        this.minecraft.player.getInventory().fillStackedContents(this.recipeFinder);
        String string = this.searchBox != null ? this.searchBox.getValue() : "";
        Font var10003 = this.minecraft.font;
        int var10004 = i + 26;
        int var10005 = j + 14;
        Objects.requireNonNull(this.minecraft.font);
        this.searchBox = new EditBox(var10003, var10004, var10005, 79, 9 + 3, Component.translatable("itemGroup.search"));
        ((RecipeBookComponentAccessor) this).setSearchBox(searchBox); // fix crash due to super.charTyped
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(true);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setValue(string);
        this.searchBox.setHint(SEARCH_HINT);

        this.recipesPage.initialize(this.minecraft, i, j, smithingScreenHandler, leftOffset);
        this.tabButtons.clear();
        this.recipeBook.setFilteringCraftable(BetterRecipeBook.rememberedBrewingToggle);
        this.toggleSmithableButton = new StateSwitchingButton(i + 110, j + 12, 26, 16, this.recipeBook.isFilteringCraftable());
        this.setBookButtonTexture();

        for (SmithingRecipeBookGroup smithingRecipeBookGroup : SmithingRecipeBookGroup.getGroups()) {
            this.tabButtons.add(new SmithingRecipeGroupButtonWidget(smithingRecipeBookGroup));
        }

        if (this.currentTab != null) {
            this.currentTab = this.tabButtons.stream().filter((button) -> button.getGroup().equals(this.currentTab.getGroup())).findFirst().orElse(null);
        }

        if (this.currentTab == null) {
            this.currentTab = this.tabButtons.get(0);
        }

        if (BetterRecipeBook.config.settingsButton) {
            this.settingsButton = new ImageButton(i + 11, j + 137, 18, 18, BRBTextures.SETTINGS_BUTTON_SPRITES, button -> {
                Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, Minecraft.getInstance().screen).get());
            });
        }

        this.currentTab.setStateTriggered(true);
        this.refreshResults(false);
        this.refreshTabButtons();
    }

    private void refreshTabButtons() {
        int i = (this.width - 147) / 2 - this.leftOffset - 30;
        int j = (this.height - 166) / 2 + 3;
        int l = 0;

        for (SmithingRecipeGroupButtonWidget smithingRecipeGroupButtonWidget : this.tabButtons) {
            SmithingRecipeBookGroup smithingRecipeBookGroup = smithingRecipeGroupButtonWidget.getGroup();
            if (smithingRecipeBookGroup == SmithingRecipeBookGroup.SMITHING_SEARCH) {
                smithingRecipeGroupButtonWidget.visible = true;
            }
            smithingRecipeGroupButtonWidget.setPosition(i, j + 27 * l++);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        if (!this.isOpen()) return;

        if (doubleRefresh) {
            // Minecraft doesn't populate the inventory on initialization so this is the only solution I have
            refreshResults(true);
            doubleRefresh = false;
        }

        gui.pose().pushPose();
        gui.pose().translate(0.0f, 0.0f, 100.0f);

        // blit recipe book background texture
        int blitX = (this.width - 147) / 2 - this.leftOffset;
        int blitY = (this.height - 166) / 2;
        gui.blit(BRBTextures.RECIPE_BOOK_BACKGROUND_TEXTURE, blitX, blitY, 1, 1, 147, 166);

        // render search box
        this.searchBox.render(gui, mouseX, mouseY, delta);

        // render tab buttons
        for (SmithingRecipeGroupButtonWidget smithingRecipeGroupButtonWidget : this.tabButtons) {
            smithingRecipeGroupButtonWidget.render(gui, mouseX, mouseY, delta);
        }

        this.toggleSmithableButton.render(gui, mouseX, mouseY, delta);

        // render the BRB settings button
        if (BetterRecipeBook.config.settingsButton) {
            this.settingsButton.render(gui, mouseX, mouseY, delta);
        }

        // render the recipe book page contents
        this.recipesPage.render(gui, blitX, blitY, mouseX, mouseY, delta);

        gui.pose().popPose();
    }

    private void refreshResults(boolean resetCurrentPage) {
        if (this.currentTab == null) return;
        if (this.searchBox == null) return;

        // Create a copy to not mess with the original list
        List<SmithingRecipeCollection> results = new ArrayList<>(recipeBook.getCollectionsForCategory(currentTab.getGroup(), smithingScreenHandler, registryAccess, recipeManager));

        String string = this.searchBox.getValue();
        if (!string.isEmpty()) {
            results.removeIf(collection -> !collection.getFirst().getTemplateType().toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)));
        }

        if (this.recipeBook.isFilteringCraftable()) {
            results.removeIf((result) -> !result.atleastOneCraftable(this.smithingScreenHandler.slots));
        }

        if (BetterRecipeBook.config.enablePinning) {
            List<SmithingRecipeCollection> tempResults = Lists.newArrayList(results);

            for (SmithingRecipeCollection result : tempResults) {
                if (BetterRecipeBook.pinnedRecipeManager.hasSmithing(result)) {
                    results.remove(result);
                    results.add(0, result);
                }
            }
        }

        this.recipesPage.setResults(results, resetCurrentPage, currentTab.getGroup());
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.searching = false;
        if (this.isOpen()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.setOpen(false);
                return true;
            } else {
                assert this.searchBox != null;
                if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
                    this.refreshSearchResults();
                    return true;
                } else if (this.searchBox.isFocused() && this.searchBox.isVisible()) {
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_F) {
                    if (BetterRecipeBook.config.enablePinning) {
                        for (SmithableRecipeButton resultButton : this.recipesPage.buttons) {
                            if (resultButton.isHoveredOrFocused()) {
                                BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavouriteSmithing(resultButton.getCollection().getFirst());
                                this.refreshResults(false);
                                return true;
                            }
                        }
                    }
                    return false;
                } else if (this.minecraft.options.keyChat.matches(keyCode, scanCode) && !this.searchBox.isFocused()) {
                    this.searching = true;
                    this.searchBox.setFocused(true);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return this.open ? NarrationPriority.HOVERED : NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.searching = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char chr, int modifiers) {
        if (this.searching) {
            return false;
        } else if (this.isOpen()) {
            assert this.searchBox != null;
            if (this.searchBox.charTyped(chr, modifiers)) {
                this.refreshSearchResults();
                return true;
            } else {
                return super.charTyped(chr, modifiers);
            }
        } else {
            return false;
        }
    }

    private void refreshSearchResults() {
        assert this.searchBox != null;
        String string = this.searchBox.getValue().toLowerCase(Locale.ROOT);
        if (!string.equals(this.searchText)) {
            this.refreshResults(false);
            this.searchText = string;
        }

    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.open) {
            if (this.recipesPage.mouseClicked(mouseX, mouseY, button, (this.width - 147) / 2 - ((RecipeBookComponentAccessor) this).getXOffset(), (this.height - 166) / 2, 147, 166)) {
                BRBSmithingRecipe result = this.recipesPage.getCurrentClickedRecipe();
                SmithingRecipeCollection recipeCollection = this.recipesPage.getLastClickedRecipeCollection();

                if (result != null && recipeCollection != null) {
                    this.ghostRecipe.clear();

                    if (!result.hasMaterials(this.smithingScreenHandler.slots)) {
                        this.setupGhostRecipe(result, this.smithingScreenHandler.slots);
                        return true;
                    }

                    int slotIndex = 0;
                    boolean placedBase = false;
                    for (Slot slot : smithingScreenHandler.slots) {
                        ItemStack itemStack = slot.getItem();

                        if (result.getTemplate().test(itemStack)) {
                            assert Minecraft.getInstance().gameMode != null;
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(smithingScreenHandler.containerId, smithingScreenHandler.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(smithingScreenHandler.containerId, SmithingMenu.TEMPLATE_SLOT, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                        } else if (!placedBase && ArmorTrim.getTrim(registryAccess, itemStack, true).isEmpty() && result.getBase().getItem().equals(itemStack.getItem())) {
                            assert Minecraft.getInstance().gameMode != null;
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(smithingScreenHandler.containerId, smithingScreenHandler.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(smithingScreenHandler.containerId, SmithingMenu.BASE_SLOT, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            placedBase = true;
                        } else if (result.getAddition().test(itemStack)) {
                            assert Minecraft.getInstance().gameMode != null;
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(smithingScreenHandler.containerId, smithingScreenHandler.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(smithingScreenHandler.containerId, SmithingMenu.ADDITIONAL_SLOT, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                        }

                        ++slotIndex;
                    }

                    this.refreshResults(false);
                }

                return true;
            } else {
                assert this.searchBox != null;
                if (this.searchBox.mouseClicked(mouseX, mouseY, button)) {
                    searchBox.setFocused(true);
                    searching = true;
                    return true;
                }
                searchBox.setFocused(false);
                searching = false;

                if (this.toggleSmithableButton.mouseClicked(mouseX, mouseY, button)) {
                    boolean bl = this.toggleFilteringSmithable();
                    this.toggleSmithableButton.setStateTriggered(bl);
                    BetterRecipeBook.rememberedBrewingToggle = bl;
                    this.refreshResults(false);
                    return true;
                } else if (this.settingsButton != null) {
                    if (this.settingsButton.mouseClicked(mouseX, mouseY, button) && BetterRecipeBook.config.settingsButton) {
                        return true;
                    }
                }

                Iterator<SmithingRecipeGroupButtonWidget> tabButtonsIter = this.tabButtons.iterator();

                SmithingRecipeGroupButtonWidget smithingRecipeGroupButtonWidget;
                do {
                    if (!tabButtonsIter.hasNext()) {
                        return false;
                    }

                    smithingRecipeGroupButtonWidget = tabButtonsIter.next();
                } while (!smithingRecipeGroupButtonWidget.mouseClicked(mouseX, mouseY, button));

                if (this.currentTab != smithingRecipeGroupButtonWidget) {
                    if (this.currentTab != null) {
                        this.currentTab.setStateTriggered(false);
                    }

                    this.currentTab = smithingRecipeGroupButtonWidget;
                    this.currentTab.setStateTriggered(true);
                    this.refreshResults(true);
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public void setupGhostRecipe(BRBSmithingRecipe result, List<Slot> list) {
        this.ghostRecipe.setRecipe(result);

        this.ghostRecipe.addIngredient(result.getAddition(), SmithingMenu.ADDITIONAL_SLOT_X_PLACEMENT, SmithingMenu.SLOT_Y_PLACEMENT);
        this.ghostRecipe.addIngredient(result.getTemplate(), SmithingMenu.TEMPLATE_SLOT_X_PLACEMENT, SmithingMenu.SLOT_Y_PLACEMENT);
        this.ghostRecipe.addIngredient(Ingredient.of(result.getBase()), SmithingMenu.BASE_SLOT_X_PLACEMENT, SmithingMenu.SLOT_Y_PLACEMENT);
    }

    public void renderGhostRecipe(GuiGraphics guiGraphics, int i, int j, boolean bl, float f) {
        this.ghostRecipe.render(guiGraphics, this.minecraft, i, j, bl, f);
    }

    public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
        if (this.isOpen()) {
            if (this.toggleSmithableButton.isHoveredOrFocused()) {
                Component text = this.getCraftableButtonText();
                if (this.minecraft.screen != null) {
                    gui.renderTooltip(Minecraft.getInstance().font, text, mouseX, mouseY);

                }
            }

            if (!this.recipesPage.overlay.isVisible()) {
                this.recipesPage.drawTooltip(gui, mouseX, mouseY);

                if (this.settingsButton != null) {
                    if (this.settingsButton.isHoveredOrFocused() && BetterRecipeBook.config.settingsButton) {
                        if (this.minecraft.screen != null) {
                            gui.renderTooltip(Minecraft.getInstance().font, OPEN_SETTINGS_TOOLTIP, mouseX, mouseY);
                        }
                    }
                }
            }

            renderGhostRecipeTooltip(gui, x, y, mouseX, mouseY);
        }
    }

    private void renderGhostRecipeTooltip(GuiGraphics guiGraphics, int i, int j, int k, int l) {
        ItemStack itemStack = null;

        for (int m = 0; m < this.ghostRecipe.size(); ++m) {
            SmithingGhostRecipe.SmithingGhostIngredient ghostIngredient = this.ghostRecipe.get(m);
            int n = ghostIngredient.getX() + i;
            int o = ghostIngredient.getY() + j;
            if (k >= n && l >= o && k < n + 16 && l < o + 16) {
                itemStack = ghostIngredient.getItem();
            }
        }

        if (itemStack != null && this.minecraft.screen != null) {
            guiGraphics.renderComponentTooltip(this.minecraft.font, Screen.getTooltipFromItem(this.minecraft, itemStack), k, l);
        }
    }

    public boolean isShowingGhostRecipe() {
        return this.ghostRecipe != null && this.ghostRecipe.size() > 0;
    }

    private Component getCraftableButtonText() {
        return this.toggleSmithableButton.isStateTriggered() ? this.getToggleSmithableButtonText() : ALL_RECIPES_TOOLTIP;
    }

    protected Component getToggleSmithableButtonText() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    private boolean toggleFilteringSmithable() {
        boolean bl = !this.recipeBook.isFilteringCraftable();
        this.recipeBook.setFilteringCraftable(bl);
        BetterRecipeBook.rememberedSmithableToggle = bl;
        return bl;
    }

    protected void setBookButtonTexture() {
        this.toggleSmithableButton.initTextureValues(BRBTextures.RECIPE_BOOK_FILTER_BUTTON_SPRITES);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void toggleOpen() {
        this.setOpen(!this.isOpen());
    }

    public int findLeftEdge(int width, int backgroundWidth) {
        int j;
        if (this.isOpen() && !this.narrow) {
            j = 177 + (width - backgroundWidth - 200) / 2;
        } else {
            j = (width - backgroundWidth) / 2;
        }

        return j;
    }

    static {
        SEARCH_HINT = (Component.translatable("gui.recipebook.search_hint")).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
        ONLY_CRAFTABLES_TOOLTIP = Component.translatable("brb.gui.smithable");
        ALL_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.all");
        OPEN_SETTINGS_TOOLTIP = Component.translatable("brb.gui.settings.open");
    }

}
