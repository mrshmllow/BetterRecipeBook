package marsh.town.brb.brewingstand;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.GenericRecipeButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.List;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getIngredient;

@Environment(EnvType.CLIENT)
public class BrewableRecipeButton extends GenericRecipeButton<BrewingRecipeCollection, BrewableResult, BrewingStandMenu> {
    public BrewableRecipeButton(RegistryAccess registryAccess) {
        super(registryAccess);
    }

    @Override
    protected boolean selfRecallFiltering() {
        return BetterRecipeBook.rememberedBrewingToggle;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        ItemStack inputStack = this.collection.getFirst().inputAsItemStack(category);

        builder.add(NarratedElementType.TITLE, Component.translatable("narration.recipe", inputStack.getHoverName()));
        builder.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
    }

    @Override
    public List<Component> getTooltipText() {
        List<Component> list = Lists.newArrayList();

        list.add(collection.getFirst().ingredient.getHoverName());
        PotionUtils.addPotionTooltip(collection.getFirst().ingredient, list, 1);
        list.add(Component.literal(""));

        ChatFormatting colour = ChatFormatting.DARK_GRAY;
        if (collection.getFirst().hasIngredient(menu.slots)) {
            colour = ChatFormatting.WHITE;
        }

        list.add(Component.literal(getIngredient(collection.getFirst().recipe).getItems()[0].getHoverName().getString()).withStyle(colour));

        list.add(Component.literal("↓").withStyle(ChatFormatting.DARK_GRAY));

        ItemStack inputStack = this.collection.getFirst().inputAsItemStack(category);

        if (!collection.getFirst().hasInput(category, menu.slots)) {
            colour = ChatFormatting.DARK_GRAY;
        }

        list.add(Component.literal(inputStack.getHoverName().getString()).withStyle(colour));

        this.addPinTooltip(list);

        return list;
    }
}
