package net.marshmallow.BetterRecipeBook.BrewingStand;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.marshmallow.BetterRecipeBook.Config.Config;
import net.marshmallow.BetterRecipeBook.Mixins.Accessors.BrewingRecipeRegistryRecipeAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@Environment(EnvType.CLIENT)
public class BrewingStandRecipeBookWidget extends DrawableHelper implements Drawable, Element, Selectable {
    public static final Identifier TEXTURE = new Identifier("textures/gui/recipe_book.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier("betterrecipebook:textures/gui/buttons.png");
    protected BrewingStandScreenHandler brewingStandScreenHandler;
    MinecraftClient client;
    private int parentWidth;
    private int parentHeight;
    private boolean narrow;
    ClientBrewingStandRecipeBook recipeBook;
    private int leftOffset;
    protected final BrewingRecipeBookGhostSlots ghostSlots = new BrewingRecipeBookGhostSlots();
    private boolean open;
    private final BrewingStandRecipeBookResults recipesArea = new BrewingStandRecipeBookResults();
    @Nullable
    private TextFieldWidget searchField;
    private final RecipeMatcher recipeFinder = new RecipeMatcher();
    protected ToggleButtonWidget toggleBrewableButton;
    private static final Text SEARCH_HINT_TEXT;
    private final List<BrewingRecipeGroupButtonWidget> tabButtons = Lists.newArrayList();
    @Nullable
    private BrewingRecipeGroupButtonWidget currentTab;
    private boolean searching;
    protected TexturedButtonWidget settingsButton;
    private String searchText;
    private static final Text TOGGLE_CRAFTABLE_RECIPES_TEXT;
    private static final Text TOGGLE_ALL_RECIPES_TEXT;
    private static final Text OPEN_SETTINGS_TEXT;
    boolean doubleRefresh = true;


    public void initialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow, BrewingStandScreenHandler brewingStandScreenHandler) {
        this.client = client;
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.brewingStandScreenHandler = brewingStandScreenHandler;
        this.narrow = narrow;
        assert client.player != null;
        client.player.currentScreenHandler = brewingStandScreenHandler;
        this.recipeBook = new ClientBrewingStandRecipeBook();
        this.open = BetterRecipeBook.rememberedBrewingOpen;
        // this.cachedInvChangeCount = client.player.getInventory().getChangeCount();
        this.reset();

        if (BetterRecipeBook.config.keepCentered) {
            this.leftOffset = this.narrow ? 0 : 162;
        } else {
            this.leftOffset = this.narrow ? 0 : 86;
        }

        client.keyboard.setRepeatEvents(true);
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
        this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);
        String string = this.searchField != null ? this.searchField.getText() : "";
        TextRenderer var10003 = this.client.textRenderer;
        int var10004 = i + 25;
        int var10005 = j + 14;
        Objects.requireNonNull(this.client.textRenderer);
        this.searchField = new TextFieldWidget(var10003, var10004, var10005, 80, 9 + 5, new TranslatableText("itemGroup.search"));
        this.searchField.setMaxLength(50);
        this.searchField.setDrawsBackground(false);
        this.searchField.setVisible(true);
        this.searchField.setEditableColor(16777215);
        this.searchField.setText(string);
        this.recipesArea.initialize(this.client, i, j, brewingStandScreenHandler);
        this.tabButtons.clear();
        this.recipeBook.setFilteringCraftable(BetterRecipeBook.rememberedBrewingToggle);
        this.toggleBrewableButton = new ToggleButtonWidget(i + 110, j + 12, 26, 16, this.recipeBook.isFilteringCraftable());
        this.setBookButtonTexture();

        for (BrewingRecipeBookGroup recipeBookGroup : BrewingRecipeBookGroup.getGroups()) {
            this.tabButtons.add(new BrewingRecipeGroupButtonWidget(recipeBookGroup));
        }

        if (this.currentTab != null) {
            this.currentTab = this.tabButtons.stream().filter((button) -> button.getGroup().equals(this.currentTab.getGroup())).findFirst().orElse(null);
        }

        if (this.currentTab == null) {
            this.currentTab = this.tabButtons.get(0);
        }

        if (BetterRecipeBook.config.settingsButton) {
            int u = 0;
            if (BetterRecipeBook.config.darkMode) {
                u = 18;
            }

            this.settingsButton = new TexturedButtonWidget(i + 11, j + 137, 16, 18, u, 77, 19, BUTTON_TEXTURE, button -> {
                MinecraftClient.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, MinecraftClient.getInstance().currentScreen).get());
            });
        }

        this.currentTab.setToggled(true);
        this.refreshResults(false);
        this.refreshTabButtons();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isOpen() && !Objects.requireNonNull(this.client.player).isSpectator()) {
            if (this.recipesArea.mouseClicked(mouseX, mouseY, button)) {
                BrewingResult result = this.recipesArea.getLastClickedRecipe();
                if (result != null) {
                    if (this.currentTab == null) return false;
                    this.ghostSlots.reset();

                    if (!result.hasMaterials(this.currentTab.getGroup(), brewingStandScreenHandler)) {
                        showGhostRecipe(result, brewingStandScreenHandler.slots);
                        return false;
                    }

                    Potion inputPotion = (Potion) ((BrewingRecipeRegistryRecipeAccessor<?>) result.recipe).getInput();
                    Ingredient ingredient = ((BrewingRecipeRegistryRecipeAccessor<?>) result.recipe).getIngredient();
                    Identifier identifier = Registry.POTION.getId(inputPotion);
                    ItemStack inputStack;
                    if (this.currentTab.getGroup() == BrewingRecipeBookGroup.BREWING_SPLASH_POTION) {
                        inputStack = new ItemStack(Items.SPLASH_POTION);
                    } else if (this.currentTab.getGroup() == BrewingRecipeBookGroup.BREWING_LINGERING_POTION) {
                        inputStack = new ItemStack(Items.LINGERING_POTION);
                    } else {
                        inputStack = new ItemStack(Items.POTION);
                    }

                    inputStack.getOrCreateNbt().putString("Potion", identifier.toString());

                    int slotIndex = 0;
                    int usedInputSlots = 0;
                    for (Slot slot : brewingStandScreenHandler.slots) {
                        ItemStack itemStack = slot.getStack();

                        assert inputStack.getNbt() != null;
                        if (inputStack.getNbt().equals(itemStack.getNbt()) && inputStack.getItem().equals(itemStack.getItem())) {
                            if (usedInputSlots <= 2) {
                                System.out.println(usedInputSlots);
                                assert MinecraftClient.getInstance().interactionManager != null;
                                MinecraftClient.getInstance().interactionManager.clickSlot(brewingStandScreenHandler.syncId, brewingStandScreenHandler.getSlot(slotIndex).id, 0, SlotActionType.PICKUP, MinecraftClient.getInstance().player);
                                MinecraftClient.getInstance().interactionManager.clickSlot(brewingStandScreenHandler.syncId, brewingStandScreenHandler.getSlot(usedInputSlots).id, 0, SlotActionType.PICKUP, MinecraftClient.getInstance().player);
                                ++usedInputSlots;
                            }
                        } else if (ingredient.getMatchingStacks()[0].getItem().equals(slot.getStack().getItem())) {
                            assert MinecraftClient.getInstance().interactionManager != null;
                            MinecraftClient.getInstance().interactionManager.clickSlot(brewingStandScreenHandler.syncId, brewingStandScreenHandler.getSlot(slotIndex).id, 0, SlotActionType.PICKUP, MinecraftClient.getInstance().player);
                            MinecraftClient.getInstance().interactionManager.clickSlot(brewingStandScreenHandler.syncId, brewingStandScreenHandler.getSlot(3).id, 0, SlotActionType.PICKUP, MinecraftClient.getInstance().player);
                        }

                        ++slotIndex;
                    }

                    this.refreshResults(false);
                }

                return true;
            } else {
                assert this.searchField != null;
                if (this.searchField.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                } else if (this.toggleBrewableButton.mouseClicked(mouseX, mouseY, button)) {
                    boolean bl = this.toggleFilteringBrewable();
                    this.toggleBrewableButton.setToggled(bl);
                    BetterRecipeBook.rememberedBrewingToggle = bl;
                    this.refreshResults(false);
                    return true;
                } else if (this.settingsButton != null) {
                    if (this.settingsButton.mouseClicked(mouseX, mouseY, button) && BetterRecipeBook.config.settingsButton) {
                        return true;
                    }
                }

                Iterator<BrewingRecipeGroupButtonWidget> var6 = this.tabButtons.iterator();

                BrewingRecipeGroupButtonWidget recipeGroupButtonWidget;
                do {
                    if (!var6.hasNext()) {
                        return false;
                    }

                    recipeGroupButtonWidget = var6.next();
                } while (!recipeGroupButtonWidget.mouseClicked(mouseX, mouseY, button));

                if (this.currentTab != recipeGroupButtonWidget) {
                    if (this.currentTab != null) {
                        this.currentTab.setToggled(false);
                    }

                    this.currentTab = recipeGroupButtonWidget;
                    this.currentTab.setToggled(true);
                    this.refreshResults(true);
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public void showGhostRecipe(BrewingResult result, List<Slot> slots) {
        this.ghostSlots.addSlot(((BrewingRecipeRegistryRecipeAccessor<?>) result.recipe).getIngredient().getMatchingStacks()[0], slots.get(3).x, slots.get(3).y);

        assert currentTab != null;
        ItemStack inputStack = result.inputAsItemStack(currentTab.getGroup());
        this.ghostSlots.addSlot(result.ingredient, slots.get(0).x, slots.get(0).y);
        this.ghostSlots.addSlot(inputStack, slots.get(1).x, slots.get(1).y);
        this.ghostSlots.addSlot(inputStack, slots.get(2).x, slots.get(2).y);
    }


    private boolean toggleFilteringBrewable() {
        boolean bl = !this.recipeBook.isFilteringCraftable();
        this.recipeBook.setFilteringCraftable(bl);
        BetterRecipeBook.rememberedBrewingToggle = bl;
        return bl;
    }

    private void refreshResults(boolean resetCurrentPage) {
        assert currentTab != null;
        List<BrewingResult> results = recipeBook.getResultsForCategory(currentTab.getGroup());

        assert this.searchField != null;
        String string = this.searchField.getText();
        if (!string.isEmpty()) {
            results.removeIf(itemStack -> !itemStack.ingredient.getName().getString().toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)));
        }

        if (this.recipeBook.isFilteringCraftable()) {
            results.removeIf((brewingResult) -> !brewingResult.hasMaterials(currentTab.getGroup(), brewingStandScreenHandler));
        }

        List<BrewingResult> tempResults = Lists.newArrayList(results);

        for (BrewingResult brewingResult : tempResults) {
            if (BetterRecipeBook.pinnedRecipeManager.hasPotion(brewingResult.recipe)) {
                results.remove(brewingResult);
                results.add(0, brewingResult);
            }
        }

        this.recipesArea.setResults(results, resetCurrentPage, currentTab.getGroup());
    }

    private void refreshTabButtons() {
        int i = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
        int j = (this.parentHeight - 166) / 2 + 3;
        int l = 0;

        for (BrewingRecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
            BrewingRecipeBookGroup recipeBookGroup = recipeGroupButtonWidget.getGroup();
            if (recipeBookGroup == BrewingRecipeBookGroup.BREWING_SEARCH) {
                recipeGroupButtonWidget.visible = true;
            }
            recipeGroupButtonWidget.setPos(i, j + 27 * l++);
        }
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (doubleRefresh) {
            // Minecraft doesn't populate the inventory on initialization so this is the only solution I have
            refreshResults(true);
            doubleRefresh = false;
        }
        if (this.isOpen()) {
            matrices.push();
            matrices.translate(0.0D, 0.0D, 100.0D);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int i = (this.parentWidth - 147) / 2 - this.leftOffset;
            int j = (this.parentHeight - 166) / 2;
            this.drawTexture(matrices, i, j, 1, 1, 147, 166);
            assert this.searchField != null;
            if (!this.searchField.isFocused() && this.searchField.getText().isEmpty()) {
                drawTextWithShadow(matrices, this.client.textRenderer, SEARCH_HINT_TEXT, i + 25, j + 14, -1);
            } else {
                this.searchField.render(matrices, mouseX, mouseY, delta);
            }

            for (BrewingRecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
                recipeGroupButtonWidget.render(matrices, mouseX, mouseY, delta);
            }

            this.toggleBrewableButton.render(matrices, mouseX, mouseY, delta);

            if (BetterRecipeBook.config.settingsButton) {
                this.settingsButton.render(matrices, mouseX, mouseY, delta);
            }

            this.recipesArea.draw(matrices, i, j, mouseX, mouseY, delta);
            matrices.pop();
        }
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

    public void drawGhostSlots(MatrixStack matrices, int x, int y, boolean bl, float delta) {
        this.ghostSlots.draw(matrices, this.client, x, y, bl, delta);
    }

    private void setOpen(boolean opened) {
        if (opened) {
            this.reset();
        }
        this.open = opened;
    }

    public void toggleOpen() {
        this.setOpen(!this.isOpen());
    }

    protected void setBookButtonTexture() {
        this.toggleBrewableButton.setTextureUV(152, 41, 28, 18, TEXTURE);
    }

    @Override
    public SelectionType getType() {
        return this.open ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
    }

    public void drawTooltip(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (this.isOpen()) {
            this.recipesArea.drawTooltip(matrices, mouseX, mouseY);
            if (this.toggleBrewableButton.isHovered()) {
                Text text = this.getCraftableButtonText();
                if (this.client.currentScreen != null) {
                    this.client.currentScreen.renderTooltip(matrices, text, mouseX, mouseY);
                }
            }

            if (this.settingsButton != null) {
                if (this.settingsButton.isHovered() && BetterRecipeBook.config.settingsButton) {
                    if (this.client.currentScreen != null) {
                        this.client.currentScreen.renderTooltip(matrices, OPEN_SETTINGS_TEXT, mouseX, mouseY);
                    }
                }
            }

            this.drawGhostSlotTooltip(matrices, x, y, mouseX, mouseY);
        }
    }

    private Text getCraftableButtonText() {
        return this.toggleBrewableButton.isToggled() ? this.getToggleCraftableButtonText() : TOGGLE_ALL_RECIPES_TEXT;
    }

    protected Text getToggleCraftableButtonText() {
        return TOGGLE_CRAFTABLE_RECIPES_TEXT;
    }

    private void drawGhostSlotTooltip(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        ItemStack itemStack = null;

        for(int i = 0; i < this.ghostSlots.getSlotCount(); ++i) {
            BrewingRecipeBookGhostSlots.GhostSlot ghostInputSlot = this.ghostSlots.getSlot(i);
            int j = ghostInputSlot.getX() + x;
            int k = ghostInputSlot.getY() + y;
            if (mouseX >= j && mouseY >= k && mouseX < j + 16 && mouseY < k + 16) {
                itemStack = ghostInputSlot.getCurrentItemStack();
            }
        }

        if (itemStack != null && this.client.currentScreen != null) {
            this.client.currentScreen.renderTooltip(matrices, this.client.currentScreen.getTooltipFromItem(itemStack), mouseX, mouseY);
        }

    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.searching = false;
        if (this.isOpen() && !Objects.requireNonNull(this.client.player).isSpectator()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.setOpen(false);
                return true;
            } else {
                assert this.searchField != null;
                if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
                    this.refreshSearchResults();
                    return true;
                } else if (this.searchField.isFocused() && this.searchField.isVisible()) {
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_F) {
                    for (BrewingAnimatedResultButton resultButton : this.recipesArea.resultButtons) {
                        if (resultButton.isHovered()) {
                            BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavouritePotion(resultButton.getRecipe().recipe);
                            this.refreshResults(false);
                            return true;
                        }
                    }
                    return false;
                } else if (this.client.options.keyChat.matchesKey(keyCode, scanCode) && !this.searchField.isFocused()) {
                    this.searching = true;
                    this.searchField.setTextFieldFocused(true);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.searching = false;
        return Element.super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char chr, int modifiers) {
        if (this.searching) {
            return false;
        } else if (this.isOpen() && !Objects.requireNonNull(this.client.player).isSpectator()) {
            assert this.searchField != null;
            if (this.searchField.charTyped(chr, modifiers)) {
                this.refreshSearchResults();
                return true;
            } else {
                return Element.super.charTyped(chr, modifiers);
            }
        } else {
            return false;
        }
    }

    private void refreshSearchResults() {
        assert this.searchField != null;
        String string = this.searchField.getText().toLowerCase(Locale.ROOT);
        if (!string.equals(this.searchText)) {
            this.refreshResults(false);
            this.searchText = string;
        }

    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    static {
        SEARCH_HINT_TEXT = (new TranslatableText("gui.recipebook.search_hint")).formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
        TOGGLE_CRAFTABLE_RECIPES_TEXT = new TranslatableText("betterrecipebook.gui.togglePotions.brewable");
        TOGGLE_ALL_RECIPES_TEXT = new TranslatableText("gui.recipebook.toggleRecipes.all");
        OPEN_SETTINGS_TEXT = new TranslatableText("betterrecipebook.gui.settings.open");
    }
}
