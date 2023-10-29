package marsh.town.brb.smithingtable;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.brewingstand.BrewableAnimatedResultButton;
import marsh.town.brb.config.Config;
import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
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
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class SmithingRecipeBookComponent extends RecipeBookComponent {
    private boolean open;
    private Minecraft client;
    private int parentWidth;
    private int parentHeight;
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
    public final SmithingRecipeBookResults recipesArea = new SmithingRecipeBookResults();
    private final List<SmithingRecipeGroupButtonWidget> tabButtons = Lists.newArrayList();
    @Nullable
    public SmithingRecipeGroupButtonWidget currentTab;
    protected StateSwitchingButton toggleSmithableButton;
    private ImageButton settingsButton;
    boolean doubleRefresh = true;
    private boolean searching;
    private String searchText;

    public void initialize(int parentWidth, int parentHeight, boolean narrow, SmithingMenu smithingScreenHandler) {
        this.client = Minecraft.getInstance();
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.smithingScreenHandler = smithingScreenHandler;
        this.narrow = narrow;
        client.player.containerMenu = smithingScreenHandler;
        this.recipeBook = new SmithingClientRecipeBook();
        this.open = BetterRecipeBook.rememberedSmithingOpen;
        // this.cachedInvChangeCount = client.player.getInventory().getChangeCount();
        this.reset();

        if (BetterRecipeBook.config.keepCentered) {
            this.leftOffset = this.narrow ? 0 : 162;
        } else {
            this.leftOffset = this.narrow ? 0 : 86;
        }

        // this code is responsible for selectively rendering ghost slots
//        ghostRecipe.setRenderingPredicate((type, ingredient) -> {
//            ItemStack real = brewingStandScreenHandler.slots.get(ingredient.getContainerSlot()).getItem();
//            switch (type) {
//                case ITEM:
//                case BACKGROUND:
//                    // slot 0 is the preview so map it to 1
//                    ItemStack fake = ingredient.getContainerSlot() == 0 ? ingredient.getOwner().getBySlot(1).getItem() : ingredient.getItem();
//
//                    // if the ingredient is in one of the output slots
//                    if (ingredient.getContainerSlot() < 3) {
//                        if (real.getItem() instanceof PotionItem) {
//                            Potion realPotion = PotionUtils.getPotion(real);
//                            Potion fakePotion = PotionUtils.getPotion(fake);
//
//                            return !realPotion.equals(fakePotion);
//                        } else { // else it's not valid
//                            return true;
//                        }
//                    } else { // else it's the consumable item
//                        return !real.is(fake.getItem());
//                    }
//                case TOOLTIP:
//                    return real.isEmpty();
//                default:
//                    return true;
//            }
//        });

        // still required?
        //client.keyboardHandler.setSendRepeatsToGui(true);
    }

    public void reset() {
        if (BetterRecipeBook.config.keepCentered) {
            this.leftOffset = this.narrow ? 0 : 162;
        } else {
            this.leftOffset = this.narrow ? 0 : 86;
        }

        int i = (this.parentWidth - 147) / 2 - this.leftOffset;
        int j = (this.parentHeight - 166) / 2;
        this.recipeFinder.clear();
        assert this.client.player != null;
        this.client.player.getInventory().fillStackedContents(this.recipeFinder);
        String string = this.searchBox != null ? this.searchBox.getValue() : "";
        Font var10003 = this.client.font;
        int var10004 = i + 26;
        int var10005 = j + 14;
        Objects.requireNonNull(this.client.font);
        this.searchBox = new EditBox(var10003, var10004, var10005, 79, 9 + 3, Component.translatable("itemGroup.search"));
        ((RecipeBookComponentAccessor) this).setSearchBox(searchBox); // fix crash due to super.charTyped
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(true);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setValue(string);
        this.searchBox.setHint(SEARCH_HINT);

        this.recipesArea.initialize(this.client, i, j, smithingScreenHandler);
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
        int i = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
        int j = (this.parentHeight - 166) / 2 + 3;
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
        int blitX = (this.parentWidth - 147) / 2 - this.leftOffset;
        int blitY = (this.parentHeight - 166) / 2;
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
        this.recipesArea.render(gui, blitX, blitY, mouseX, mouseY, delta);

        gui.pose().popPose();
    }

    private void refreshResults(boolean resetCurrentPage) {
        if (this.currentTab == null) return;
        if (this.searchBox == null) return;

        // Create a copy to not mess with the original list
        List<SmithableResult> results = new ArrayList<>(recipeBook.getResultsForCategory(currentTab.getGroup()));

        String string = this.searchBox.getValue();
        if (!string.isEmpty()) {
            results.removeIf(itemStack -> !itemStack.result.getHoverName().getString().toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)));
        }

        if (this.recipeBook.isFilteringCraftable()) {
            results.removeIf((result) -> !result.hasMaterials(currentTab.getGroup(), smithingScreenHandler.slots));
        }

        if (BetterRecipeBook.config.enablePinning) {
            List<SmithableResult> tempResults = Lists.newArrayList(results);

            for (SmithableResult result : tempResults) {
//                if (BetterRecipeBook.pinnedRecipeManager.hasPotion(result.recipe)) {
//                    results.remove(result);
//                    results.add(0, result);
//                }
            }
        }

        this.recipesArea.setResults(results, resetCurrentPage, currentTab.getGroup());
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
                        for (SmithableAnimatedResultButton resultButton : this.recipesArea.buttons) {
                            if (resultButton.isHoveredOrFocused()) {
//                                BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavouritePotion(resultButton.getRecipe().recipe);
                                this.refreshResults(false);
                                return true;
                            }
                        }
                    }
                    return false;
                } else if (this.client.options.keyChat.matches(keyCode, scanCode) && !this.searchBox.isFocused()) {
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
            if (this.recipesArea.mouseClicked(mouseX, mouseY, button)) {
                SmithableResult result = this.recipesArea.getCurrentClickedRecipe();
                if (result != null) {
                    this.ghostRecipe.clear();

                    if (!result.hasMaterials(this.currentTab.getGroup(), smithingScreenHandler.slots)) {
//                        showGhostRecipe(result, brewingStandScreenHandler.slots);
                        return true;
                    }

                    int slotIndex = 0;
                    int usedInputSlots = 0;
                    for (Slot slot : smithingScreenHandler.slots) {
                        ItemStack itemStack = slot.getItem();

                        if (result.template.test(itemStack)) {
                            assert Minecraft.getInstance().gameMode != null;
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(smithingScreenHandler.containerId, smithingScreenHandler.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(smithingScreenHandler.containerId, SmithingMenu.TEMPLATE_SLOT, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            ++usedInputSlots;
                        } else if (result.base.test(itemStack)) {
                            assert Minecraft.getInstance().gameMode != null;
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(smithingScreenHandler.containerId, smithingScreenHandler.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            Minecraft.getInstance().gameMode.handleInventoryMouseClick(smithingScreenHandler.containerId, SmithingMenu.BASE_SLOT, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                            ClientInventoryUtil.storeItem(-1, i -> i > 4);
                        } else if (result.addition.test(itemStack)) {
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

                Iterator<SmithingRecipeGroupButtonWidget> var6 = this.tabButtons.iterator();

                SmithingRecipeGroupButtonWidget smithingRecipeGroupButtonWidget;
                do {
                    if (!var6.hasNext()) {
                        return false;
                    }

                    smithingRecipeGroupButtonWidget = var6.next();
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

    public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
        if (this.isOpen()) {
            this.recipesArea.drawTooltip(gui, mouseX, mouseY);
            if (this.toggleSmithableButton.isHoveredOrFocused()) {
                Component text = this.getCraftableButtonText();
                if (this.client.screen != null) {
                    gui.renderTooltip(Minecraft.getInstance().font, text, mouseX, mouseY);

                }
            }

            if (this.settingsButton != null) {
                if (this.settingsButton.isHoveredOrFocused() && BetterRecipeBook.config.settingsButton) {
                    if (this.client.screen != null) {
                        gui.renderTooltip(Minecraft.getInstance().font, OPEN_SETTINGS_TOOLTIP, mouseX, mouseY);
                    }
                }
            }

//            ghostRecipe.renderTooltip(gui, x, y, mouseX, mouseY);
        }
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
