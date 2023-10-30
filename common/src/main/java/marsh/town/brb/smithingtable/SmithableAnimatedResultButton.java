package marsh.town.brb.smithingtable;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class SmithableAnimatedResultButton extends AbstractWidget {
    private SmithableResult smithingRecipe;
    private SmithingRecipeBookGroup group;
    private SmithingMenu smithingMenu;
    private float time;
    private int currentIndex;

    public SmithableAnimatedResultButton() {
        super(0, 0, 25, 25, CommonComponents.EMPTY);
    }

    public void showSmithableRecipe(SmithableResult potionRecipe, SmithingRecipeBookGroup group, SmithingMenu smithingMenu) {
        this.smithingRecipe = potionRecipe;
        this.group = group;
        this.smithingMenu = smithingMenu;
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        if (!Screen.hasControlDown()) {
            this.time += delta;
        }

        // blit outline texture
        ResourceLocation outlineTexture = smithingRecipe.hasMaterials(group, smithingMenu.slots) ?
                BRBTextures.RECIPE_BOOK_BUTTON_SLOT_CRAFTABLE_SPRITE : BRBTextures.RECIPE_BOOK_BUTTON_SLOT_UNCRAFTABLE_SPRITE;
        gui.blitSprite(outlineTexture, getX(), getY(), this.width, this.height);

        List<Holder.Reference<TrimMaterial>> list = smithingRecipe.getPossibleTrims(Minecraft.getInstance().getConnection().registryAccess());

        ItemStack result = smithingRecipe.getTrimmedResult(getCurrentTrimMaterial(), Minecraft.getInstance().getConnection().registryAccess());

        this.currentIndex = Mth.floor(this.time / 30.0F) % list.size();

        // render ingredient item
        int offset = 4;
        gui.renderFakeItem(result, getX() + offset, getY() + offset);

        // if pinned recipe, blit the pin texture over it
        if (BetterRecipeBook.config.enablePinning && BetterRecipeBook.pinnedRecipeManager.hasSmithing(smithingRecipe)) {
            gui.blitSprite(BRBTextures.RECIPE_BOOK_PIN_SPRITE, getX() - 4, getY() - 4, 32, 32);
        }
    }

    private Holder.Reference<TrimMaterial> getCurrentTrimMaterial() {
        List<Holder.Reference<TrimMaterial>> list = smithingRecipe.getPossibleTrims(Minecraft.getInstance().getConnection().registryAccess());

        return list.get(currentIndex);
    }

    public SmithableResult getRecipe() {
        return smithingRecipe;
    }

    public void updateWidgetNarration(NarrationElementOutput builder) {
//        ItemStack inputStack = this.smithingRecipe.inputAsItemStack(group);

//        builder.add(NarratedElementType.TITLE, Component.translatable("narration.recipe", inputStack.getHoverName()));
//        builder.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
    }

    public List<Component> getTooltipText() {
        List<Component> list = Lists.newArrayList();

        list.addAll(smithingRecipe.getTrimmedResult(getCurrentTrimMaterial(), Minecraft.getInstance().getConnection().registryAccess()).getTooltipLines(Minecraft.getInstance().player, TooltipFlag.NORMAL));
        list.add(Component.literal(""));

        ChatFormatting colour = ChatFormatting.DARK_GRAY;
        if (smithingRecipe.hasTemplate(smithingMenu.slots)) {
            colour = ChatFormatting.WHITE;
        }

        list.add(Component.literal(smithingRecipe.template.getItems()[0].getHoverName().getString()).withStyle(colour));

        colour = ChatFormatting.DARK_GRAY;
        if (smithingRecipe.hasTemplate(smithingMenu.slots)) {
            colour = ChatFormatting.GRAY;
        }

        list.add(Component.literal(smithingRecipe.getTemplateType()).withStyle(colour));

        list.add(Component.literal("+").withStyle(ChatFormatting.DARK_GRAY));

        colour = ChatFormatting.DARK_GRAY;
        if (smithingRecipe.hasBase(smithingMenu.slots)) {
            colour = ChatFormatting.WHITE;
        }

        list.add(Component.literal(smithingRecipe.base.getHoverName().getString()).withStyle(colour));

        list.add(Component.literal("+").withStyle(ChatFormatting.DARK_GRAY));

        colour = ChatFormatting.DARK_GRAY;
        if (smithingRecipe.hasAddition(smithingMenu.slots)) {
            colour = ChatFormatting.WHITE;
        }

        Ingredient.Value addition = smithingRecipe.addition.values[0];

        if (addition instanceof Ingredient.ItemValue) {
            list.add(Component.literal(smithingRecipe.addition.getItems()[0].getHoverName().getString()).withStyle(colour));
        } else {
            ItemStack result = new ItemStack(getCurrentTrimMaterial().value().ingredient().value());

            list.add(result.getHoverName().copy().withStyle(colour));
        }

        if (BetterRecipeBook.config.enablePinning) {
            if (BetterRecipeBook.pinnedRecipeManager.hasSmithing(smithingRecipe)) {
                list.add(Component.translatable("brb.gui.pin.remove"));
            } else {
                list.add(Component.translatable("brb.gui.pin.add"));
            }
        }

        return list;
    }
}
