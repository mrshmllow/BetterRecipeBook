package marsh.town.brb.BrewingStand;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

import static marsh.town.brb.BrewingStand.PlatformPotionUtil.getFrom;
import static marsh.town.brb.BrewingStand.PlatformPotionUtil.getIngredient;

public class Result {
    public ItemStack ingredient;
    public PotionBrewing.Mix<?> recipe;
    public ResourceLocation input;

    public Result(ItemStack ingredient, PotionBrewing.Mix<?> recipe) {
        this.ingredient = ingredient;
        this.recipe = recipe;
        this.input = BuiltInRegistries.POTION.getKey(getFrom(recipe));
    }

    public boolean hasIngredient(BrewingStandMenu handledScreen) {
        for (ItemStack itemStack : getIngredient(recipe).getItems()) {
            for (Slot slot : handledScreen.slots) {
                if (itemStack.getItem().equals(slot.getItem().getItem())) return true;
            }
        }
        return false;
    }

    public ItemStack inputAsItemStack(RecipeBookGroup group) {
        Potion inputPotion = getFrom(recipe);

        ResourceLocation identifier = BuiltInRegistries.POTION.getKey(inputPotion);
        ItemStack inputStack;
        if (group == RecipeBookGroup.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (group == RecipeBookGroup.BREWING_LINGERING_POTION) {
            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateTag().putString("Potion", identifier.toString());
        return inputStack;
    }

    public boolean hasInput(RecipeBookGroup group, BrewingStandMenu handledScreen) {
        ItemStack inputStack = inputAsItemStack(group);

        for (Slot slot : handledScreen.slots) {
            ItemStack itemStack = slot.getItem();

            if (inputStack.getTag() == null) return false;
            if (inputStack.getTag().equals(itemStack.getTag()) && inputStack.getItem().equals(itemStack.getItem())) return true;
        }

        return false;
    }

    public boolean hasMaterials(RecipeBookGroup group, BrewingStandMenu handledScreen) {
        boolean hasIngredient = hasIngredient(handledScreen);
        boolean hasInput = hasInput(group, handledScreen);

        return hasIngredient && hasInput;
    }
}
