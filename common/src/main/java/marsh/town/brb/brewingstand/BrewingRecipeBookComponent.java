package marsh.town.brb.brewingstand;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.api.BRBBookSettings;
import marsh.town.brb.generic.GenericRecipeBookComponent;
import marsh.town.brb.generic.GenericRecipeButton;
import marsh.town.brb.generic.GenericRecipePage;
import marsh.town.brb.interfaces.IPinningComponent;
import marsh.town.brb.loaders.PotionLoader;
import marsh.town.brb.mixins.accessors.BrewingStandMenuAccessor;
import marsh.town.brb.util.BRBHelper;
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
import java.util.function.Consumer;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getFrom;
import static marsh.town.brb.brewingstand.PlatformPotionUtil.getIngredient;

@Environment(EnvType.CLIENT)
public class BrewingRecipeBookComponent extends GenericRecipeBookComponent<BrewingStandMenu, BrewingRecipeCollection, BrewableResult> implements IPinningComponent<BrewingRecipeCollection> {
    private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("brb.gui.togglePotions.brewable");

    public void init(int parentWidth, int parentHeight, Minecraft client, boolean narrow, BrewingStandMenu brewingStandScreenHandler, RegistryAccess registryAccess, BRBHelper.Book book) {
        this.init(parentWidth, parentHeight, client, narrow, brewingStandScreenHandler, null, registryAccess, book);
    }

    @Override
    public void init(int parentWidth, int parentHeight, Minecraft client, boolean narrow, BrewingStandMenu menu, Consumer<ItemStack> onGhostRecipeUpdate, RegistryAccess registryAccess, BRBHelper.Book book) {
        super.init(parentWidth, parentHeight, client, narrow, menu, onGhostRecipeUpdate, registryAccess, book);

        this.recipesPage = new GenericRecipePage<>(registryAccess, () -> new BrewableRecipeButton(registryAccess, () -> BRBBookSettings.isFiltering(this.getRecipeBookType())));
        // this.cachedInvChangeCount = client.player.getInventory().getChangeCount();

//        if (this.isVisible()) {
        this.initVisuals();
//        }

        ghostRecipe.setRenderingPredicate((type, ingredient) -> {
            ItemStack slot = menu.slots.get(ingredient.getContainerSlot()).getItem();
            switch (type) {
                case ITEM, BACKGROUND -> {
                    // slot 0 is the result so map it to 1
                    ItemStack ghost = ingredient.getContainerSlot() == BrewingStandMenuAccessor.getBOTTLE_SLOT_START() ? ingredient.getOwner().getBySlot(1).getItem() : ingredient.getItem();

                    // slot is result
                    if (ingredient.getContainerSlot() >= BrewingStandMenuAccessor.getBOTTLE_SLOT_START() && ingredient.getContainerSlot() <= BrewingStandMenuAccessor.getBOTTLE_SLOT_END()) {
                        if (!(slot.getItem() instanceof PotionItem)) return true;

                        Potion slotPotion = PotionUtils.getPotion(slot);
                        Potion ghostPotion = PotionUtils.getPotion(ghost);

                        return !slotPotion.equals(ghostPotion);
                    } else { // else it's the consumable item
                        return !slot.is(ghost.getItem());
                    }
                }
                case TOOLTIP -> {
                    // render tooltip only if slot is empty
                    return slot.isEmpty();
                }
            }
            return true;
        });

        // still required?
        //client.keyboardHandler.setSendRepeatsToGui(true);
    }

    public ItemStack getInputStack(BrewableResult result) {
        Potion inputPotion = getFrom(result.recipe);
        Ingredient ingredient = getIngredient(result.recipe);
        ResourceLocation identifier = BuiltInRegistries.POTION.getKey(inputPotion);
        ItemStack inputStack;
        if (this.selectedTab.getCategory() == BetterRecipeBook.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (this.selectedTab.getCategory() == BetterRecipeBook.BREWING_LINGERING_POTION) {
            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateTag().putString("Potion", identifier.toString());
        return inputStack;
    }

    public void setupGhostRecipe(BrewableResult result, List<Slot> slots) {
        this.ghostRecipe.addIngredient(BrewingStandMenuAccessor.getINGREDIENT_SLOT(), Ingredient.of(getIngredient(result.recipe).getItems()[0]), slots.get(BrewingStandMenuAccessor.getINGREDIENT_SLOT()).x, slots.get(BrewingStandMenuAccessor.getINGREDIENT_SLOT()).y);

        assert selectedTab != null;
        ItemStack inputStack = result.inputAsItemStack(selectedTab.getCategory());

        for (int i = BrewingStandMenuAccessor.getBOTTLE_SLOT_START(); i <= BrewingStandMenuAccessor.getBOTTLE_SLOT_END(); i++) {
            this.ghostRecipe.addIngredient(i, Ingredient.of(inputStack), slots.get(i).x, slots.get(i).y);
        }
    }

    @Override
    protected List<BrewingRecipeCollection> getCollectionsForCategory() {
        List<BrewingRecipeCollection> results = new ArrayList<>();
        BRBBookCategories.Category category = selectedTab.getCategory();

        if (category == BetterRecipeBook.BREWING_POTION) {
            for (BrewableResult potion : PotionLoader.POTIONS) {
                results.add(new BrewingRecipeCollection(List.of(potion), menu, registryAccess, category));
            }
        } else if (category == BetterRecipeBook.BREWING_SPLASH_POTION) {
            for (BrewableResult splash : PotionLoader.SPLASHES) {
                results.add(new BrewingRecipeCollection(List.of(splash), menu, registryAccess, category));
            }
        } else if (category == BetterRecipeBook.BREWING_LINGERING_POTION) {
            for (BrewableResult splash : PotionLoader.LINGERINGS) {
                results.add(new BrewingRecipeCollection(List.of(splash), menu, registryAccess, category));
            }
        }

        return results;
    }

    @Override
    public void updateCollections(boolean resetCurrentPage) {
        if (this.selectedTab == null) return;
        if (this.searchBox == null) return;

        // Create a copy to not mess with the original list
        List<BrewingRecipeCollection> results = new ArrayList<>(this.getCollectionsForCategory());

        String string = this.searchBox.getValue();
        if (!string.isEmpty()) {
            results.removeIf(itemStack -> !itemStack.getFirst().ingredient.getHoverName().getString().toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)));
        }

        if (BRBBookSettings.isFiltering(BetterRecipeBook.BREWING)) {
            results.removeIf((result) -> !result.atleastOneCraftable(menu.slots));
        }

        this.betterRecipeBook$sortByPinsInPlace(results);

        this.recipesPage.setResults(results, resetCurrentPage, selectedTab.getCategory());
    }

    public void renderGhostRecipe(GuiGraphics guiGraphics, int x, int y, boolean bl, float delta) {
        this.ghostRecipe.render(guiGraphics, this.minecraft, x, y, bl, delta);
    }

    @Override
    public Component getRecipeFilterName() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    @Override
    public BRBHelper.Book getRecipeBookType() {
        return BetterRecipeBook.BREWING;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_F && BetterRecipeBook.config.enablePinning) {
            for (GenericRecipeButton<BrewingRecipeCollection, BrewableResult, BrewingStandMenu> resultButton : this.recipesPage.buttons) {
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
            setupGhostRecipe(result, menu.slots);
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
