package marsh.town.brb.brewingstand;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.enums.BRBRecipeBookType;
import marsh.town.brb.generic.GenericRecipeBookComponent;
import marsh.town.brb.generic.GenericRecipePage;
import marsh.town.brb.interfaces.IPinningComponent;
import marsh.town.brb.recipe.BRBRecipeBookCategory;
import marsh.town.brb.util.BrewingGhostRecipe;
import marsh.town.brb.util.ClientInventoryUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getFrom;
import static marsh.town.brb.brewingstand.PlatformPotionUtil.getIngredient;

@Environment(EnvType.CLIENT)
public class BrewingRecipeBookComponent extends GenericRecipeBookComponent<BrewingStandMenu, BrewingClientRecipeBook, BrewingRecipeCollection, BrewableResult, BrewableRecipeButton> implements IPinningComponent<BrewingRecipeCollection> {
    public final BrewingGhostRecipe ghostRecipe = new BrewingGhostRecipe();
    private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("brb.gui.togglePotions.brewable");

    public BrewingRecipeBookComponent() {
        super(BrewingClientRecipeBook::new);
    }

    @Override
    public void init(int parentWidth, int parentHeight, Minecraft client, boolean narrow, BrewingStandMenu brewingStandScreenHandler, RegistryAccess registryAccess) {
        super.init(parentWidth, parentHeight, client, narrow, brewingStandScreenHandler, registryAccess);

        this.recipesPage = new GenericRecipePage<>(registryAccess, () -> new BrewableRecipeButton(registryAccess, this::selfRecallFiltering));

        // this.cachedInvChangeCount = client.player.getInventory().getChangeCount();

        if (this.isVisible()) {
            this.initVisuals();
        }

        // this code is responsible for selectively rendering ghost slots
        ghostRecipe.setRenderingPredicate((type, ingredient) -> {
            ItemStack real = brewingStandScreenHandler.slots.get(ingredient.getContainerSlot()).getItem();
            switch (type) {
                case ITEM:
                case BACKGROUND:
                    // slot 0 is the preview so map it to 1
                    ItemStack fake = ingredient.getContainerSlot() == 0 ? ingredient.getOwner().getBySlot(1).getItem() : ingredient.getItem();

                    // if the ingredient is in one of the output slots
                    if (ingredient.getContainerSlot() < 3) {
                        if (real.getItem() instanceof PotionItem) {
                            Potion realPotion = PotionUtils.getPotion(real);
                            Potion fakePotion = PotionUtils.getPotion(fake);

                            return !realPotion.equals(fakePotion);
                        } else { // else it's not valid
                            return true;
                        }
                    } else { // else it's the consumable item
                        return !real.is(fake.getItem());
                    }
                case TOOLTIP:
                    return real.isEmpty();
                default:
                    return true;
            }
        });

        // still required?
        //client.keyboardHandler.setSendRepeatsToGui(true);
    }

    public ItemStack getInputStack(BrewableResult result) {
        Potion inputPotion = getFrom(result.recipe);
        Ingredient ingredient = getIngredient(result.recipe);
        ResourceLocation identifier = BuiltInRegistries.POTION.getKey(inputPotion);
        ItemStack inputStack;
        if (this.selectedTab.getCategory() == BRBRecipeBookCategory.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (this.selectedTab.getCategory() == BRBRecipeBookCategory.BREWING_LINGERING_POTION) {
            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateTag().putString("Potion", identifier.toString());
        return inputStack;
    }

    public void showGhostRecipe(BrewableResult result, List<Slot> slots) {
        this.ghostRecipe.addIngredient(3, Ingredient.of(getIngredient(result.recipe).getItems()[0]), slots.get(3).x, slots.get(3).y);

        assert selectedTab != null;
        ItemStack inputStack = result.inputAsItemStack(selectedTab.getCategory());
        this.ghostRecipe.addIngredient(0, Ingredient.of(inputStack), slots.get(0).x, slots.get(0).y);
        this.ghostRecipe.addIngredient(1, Ingredient.of(inputStack), slots.get(1).x, slots.get(1).y);
        this.ghostRecipe.addIngredient(2, Ingredient.of(inputStack), slots.get(2).x, slots.get(2).y);
    }

    public boolean toggleFiltering() {
        boolean bl = !this.book.isFilteringCraftable();
        this.book.setFilteringCraftable(bl);
        BetterRecipeBook.rememberedBrewingToggle = bl;
        return bl;
    }

    @Override
    public void updateCollections(boolean resetCurrentPage) {
        if (this.selectedTab == null) return;
        if (this.searchBox == null) return;

        // Create a copy to not mess with the original list
        List<BrewingRecipeCollection> results = new ArrayList<>(book.getCollectionsForCategory(selectedTab.getCategory(), this.menu, this.registryAccess));

        String string = this.searchBox.getValue();
        if (!string.isEmpty()) {
            results.removeIf(itemStack -> !itemStack.getFirst().ingredient.getHoverName().getString().toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)));
        }

        if (this.book.isFilteringCraftable()) {
            results.removeIf((result) -> !result.atleastOneCraftable(menu.slots));
        }

        this.betterRecipeBook$sortByPinsInPlace(results);

        this.recipesPage.setResults(results, resetCurrentPage, selectedTab.getCategory());
    }

    @Override
    protected boolean selfRecallOpen() {
        return BetterRecipeBook.rememberedBrewingOpen;
    }

    @Override
    protected boolean selfRecallFiltering() {
        return BetterRecipeBook.rememberedBrewingToggle;
    }

    public void renderGhostRecipe(GuiGraphics guiGraphics, int x, int y, boolean bl, float delta) {
        this.ghostRecipe.render(guiGraphics, this.minecraft, x, y, bl, delta);
    }

    public void toggleOpen() {
        this.setVisible(!this.isVisible());
    }

    @Override
    protected void renderGhostRecipeTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
        ghostRecipe.renderTooltip(gui, x, y, mouseX, mouseY);
    }

    @Override
    public Component getRecipeFilterName() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    @Override
    public BRBRecipeBookType getRecipeBookType() {
        return BRBRecipeBookType.BREWING;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_F && BetterRecipeBook.config.enablePinning) {
            for (BrewableRecipeButton resultButton : this.recipesPage.buttons) {
                if (resultButton.isHoveredOrFocused()) {
                    BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavourite(resultButton.getCollection());
                    this.updateCollections(false);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void handlePlaceRecipe() {
        BrewableResult result = this.recipesPage.getCurrentClickedRecipe();

        if (result == null) return;

        this.ghostRecipe.clear();

        if (!result.hasMaterials(this.selectedTab.getCategory(), menu.slots)) {
            showGhostRecipe(result, menu.slots);
            return;
        }

        ItemStack inputStack = getInputStack(result);
        Ingredient ingredient = getIngredient(result.recipe);

        int slotIndex = 0;
        int usedInputSlots = 0;
        for (Slot slot : menu.slots) {
            ItemStack itemStack = slot.getItem();

            assert inputStack.getTag() != null;
            if (inputStack.getTag().equals(itemStack.getTag()) && inputStack.getItem().equals(itemStack.getItem())) {
                if (usedInputSlots <= 2) {
                    assert Minecraft.getInstance().gameMode != null;
                    ClientInventoryUtil.storeItem(-1, i -> i > 4);
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(usedInputSlots).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                    ClientInventoryUtil.storeItem(-1, i -> i > 4);
                    ++usedInputSlots;
                }
            } else if (ingredient.getItems()[0].getItem().equals(slot.getItem().getItem())) {
                assert Minecraft.getInstance().gameMode != null;
                ClientInventoryUtil.storeItem(-1, i -> i > 4);
                Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(slotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, menu.getSlot(3).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                ClientInventoryUtil.storeItem(-1, i -> i > 4);
            }

            ++slotIndex;
        }

        this.updateCollections(false);
    }

    @Override
    public void recipesShown(List<RecipeHolder<?>> list) {

    }

    public void recipesUpdated() {
        updateCollections(false);
    }

}
