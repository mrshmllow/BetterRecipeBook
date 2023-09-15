package marsh.town.brb.brewingstand;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.List;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getIngredient;

@Environment(EnvType.CLIENT)
public class BrewableAnimatedResultButton extends AbstractWidget {
    private float time;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/recipe_book.png");
    private BrewableResult potionRecipe;
    private BrewingRecipeBookGroup group;
    private BrewingStandMenu brewingStandScreenHandler;

    public BrewableAnimatedResultButton() {
        super(0, 0, 25, 25, CommonComponents.EMPTY);
    }

    public void showPotionRecipe(BrewableResult potionRecipe, BrewingRecipeBookGroup group, BrewingStandMenu brewingStandScreenHandler) {
        this.potionRecipe = potionRecipe;
        this.group = group;
        this.brewingStandScreenHandler = brewingStandScreenHandler;
    }

    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        if (!Screen.hasControlDown()) {
            this.time += delta;
        }

        // blit outline texture
        int bgX = 29;
        int bgZ = 206;
        if (!potionRecipe.hasMaterials(group, brewingStandScreenHandler.slots)) {
            bgX += 25;
        }
        gui.blit(BACKGROUND_TEXTURE, getX(), getY(), bgX, bgZ, this.width, this.height);

        // render ingredient item
        int offset = 4;
        gui.renderFakeItem(potionRecipe.ingredient, getX() + offset, getY() + offset);

        // if pinned recipe, blit the pin texture over it
        if (BetterRecipeBook.config.enablePinning && BetterRecipeBook.pinnedRecipeManager.hasPotion(potionRecipe.recipe)) {
            gui.blit(BetterRecipeBook.PIN_TEXTURE, getX() - 3, getY() - 3, 0, 0, this.width + 3, this.height + 3, 31, 31);
        }
    }

    public BrewableResult getRecipe() {
        return potionRecipe;
    }

    public void setPos(int x, int y) {
        setX(x);
        setY(y);
    }

    public void updateWidgetNarration(NarrationElementOutput builder) {
        ItemStack inputStack = this.potionRecipe.inputAsItemStack(group);

        builder.add(NarratedElementType.TITLE, Component.translatable("narration.recipe", inputStack.getHoverName()));
        builder.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
    }

    public List<Component> getTooltipText() {
        List<Component> list = Lists.newArrayList();

        list.add(potionRecipe.ingredient.getHoverName());
        PotionUtils.addPotionTooltip(potionRecipe.ingredient, list, 1);
        list.add(Component.literal(""));

        ChatFormatting colour = ChatFormatting.DARK_GRAY;
        if (potionRecipe.hasIngredient(brewingStandScreenHandler.slots)) {
            colour = ChatFormatting.WHITE;
        }

        list.add(Component.literal(getIngredient(potionRecipe.recipe).getItems()[0].getHoverName().getString()).withStyle(colour));

        list.add(Component.literal("↓").withStyle(ChatFormatting.DARK_GRAY));

        ItemStack inputStack = this.potionRecipe.inputAsItemStack(group);

        if (!potionRecipe.hasInput(group, brewingStandScreenHandler.slots)) {
            colour = ChatFormatting.DARK_GRAY;
        }

        list.add(Component.literal(inputStack.getHoverName().getString()).withStyle(colour));

        if (BetterRecipeBook.config.enablePinning) {
            if (BetterRecipeBook.pinnedRecipeManager.hasPotion(this.potionRecipe.recipe)) {
                list.add(Component.translatable("brb.gui.pin.remove"));
            } else {
                list.add(Component.translatable("brb.gui.pin.add"));
            }
        }

        return list;
    }
}
