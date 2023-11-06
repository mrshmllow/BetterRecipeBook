package marsh.town.brb.smithingtable;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.smithingtable.recipe.BRBSmithingRecipe;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SmithableRecipeButton extends AbstractWidget {
    private SmithingRecipeCollection collection;
    private SmithingMenu smithingMenu;
    private float time;
    private int currentIndex;
    private RegistryAccess registryAccess;

    public SmithableRecipeButton(RegistryAccess registryAccess) {
        super(0, 0, 25, 25, CommonComponents.EMPTY);
        this.registryAccess = registryAccess;
    }

    public void showSmithableRecipe(SmithingRecipeCollection potionRecipe, SmithingMenu smithingMenu) {
        this.collection = potionRecipe;
        this.smithingMenu = smithingMenu;
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        if (!Screen.hasControlDown()) {
            this.time += delta;
        }

        List<BRBSmithingRecipe> list = getOrderedRecipes();

        this.currentIndex = Mth.floor(this.time / 30.0F) % list.size();

        // blit outline texture
        ResourceLocation outlineTexture = collection.atleastOneCraftable(smithingMenu.slots) ?
                BRBTextures.RECIPE_BOOK_BUTTON_SLOT_CRAFTABLE_SPRITE : BRBTextures.RECIPE_BOOK_BUTTON_SLOT_UNCRAFTABLE_SPRITE;
        gui.blitSprite(outlineTexture, getX(), getY(), this.width, this.height);

        ItemStack result = getCurrentArmour().getResult(registryAccess);

        // render ingredient item
        int offset = 4;
        gui.renderFakeItem(result, getX() + offset, getY() + offset);

        // if pinned recipe, blit the pin texture over it
        if (BetterRecipeBook.config.enablePinning && BetterRecipeBook.pinnedRecipeManager.hasSmithing(collection)) {
            gui.blitSprite(BRBTextures.RECIPE_BOOK_PIN_SPRITE, getX() - 4, getY() - 4, 32, 32);
        }
    }

    public boolean isOnlyOption() {
        return this.getOrderedRecipes().size() == 1;
    }

    private List<BRBSmithingRecipe> getOrderedRecipes() {
        List<BRBSmithingRecipe> list = this.getCollection().getDisplayRecipes(true);

        if (!BetterRecipeBook.rememberedSmithableToggle) {
            list.addAll(this.collection.getDisplayRecipes(false));
        }

        return list;
    }

    public BRBSmithingRecipe getCurrentArmour() {
        List<BRBSmithingRecipe> list = getOrderedRecipes();

        return list.get(currentIndex);
    }

    public SmithingRecipeCollection getCollection() {
        return collection;
    }

    public void updateWidgetNarration(NarrationElementOutput builder) {
//        ItemStack inputStack = this.smithingRecipe.inputAsItemStack(group);

//        builder.add(NarratedElementType.TITLE, Component.translatable("narration.recipe", inputStack.getHoverName()));
//        builder.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
    }

    public List<Component> getTooltipText() {
        List<Component> list = Lists.newArrayList();

        list.addAll(getCurrentArmour().getResult(registryAccess).getTooltipLines(Minecraft.getInstance().player, TooltipFlag.NORMAL));
        list.add(Component.literal(""));

        if (BetterRecipeBook.config.enablePinning) {
            if (BetterRecipeBook.pinnedRecipeManager.hasSmithing(collection)) {
                list.add(Component.translatable("brb.gui.pin.remove"));
            } else {
                list.add(Component.translatable("brb.gui.pin.add"));
            }
        }

        return list;
    }

    public int getWidth() {
        return 25;
    }

    protected boolean isValidClickButton(int i) {
        return i == 0 || i == 1;
    }
}
