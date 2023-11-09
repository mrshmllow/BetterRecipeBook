package marsh.town.brb.brewingstand;

import marsh.town.brb.generic.GenericRecipe;
import marsh.town.brb.recipe.BRBRecipeBookCategories;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

import java.util.List;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.*;

public class BrewableResult implements GenericRecipe {
    public ItemStack ingredient;
    public PotionBrewing.Mix<?> recipe;
    public ResourceLocation input;

    public BrewableResult(ItemStack ingredient, PotionBrewing.Mix<?> recipe) {
        this.ingredient = ingredient;
        this.recipe = recipe;
        this.input = BuiltInRegistries.POTION.getKey(getFrom(recipe));
    }

    public boolean hasIngredient(List<Slot> slots) {
        for (ItemStack itemStack : getIngredient(recipe).getItems()) {
            for (Slot slot : slots) {
                if (itemStack.getItem().equals(slot.getItem().getItem())) return true;
            }
        }
        return false;
    }

    public ItemStack inputAsItemStack(BRBRecipeBookCategories categories) {
        Potion inputPotion = getFrom(recipe);

        ResourceLocation identifier = BuiltInRegistries.POTION.getKey(inputPotion);
        ItemStack inputStack;
        if (categories == BRBRecipeBookCategories.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (categories == BRBRecipeBookCategories.BREWING_LINGERING_POTION) {
            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateTag().putString("Potion", identifier.toString());
        return inputStack;
    }

    public boolean hasInput(BRBRecipeBookCategories categories, List<Slot> slots) {
        ItemStack inputStack = inputAsItemStack(categories);

        for (Slot slot : slots) {
            ItemStack itemStack = slot.getItem();

            if (inputStack.getTag() == null) return false;
            if (inputStack.getTag().equals(itemStack.getTag()) && inputStack.getItem().equals(itemStack.getItem()))
                return true;
        }

        return false;
    }

    public boolean hasMaterials(BRBRecipeBookCategories categories, List<Slot> slots) {
        boolean hasIngredient = hasIngredient(slots);
        boolean hasInput = hasInput(categories, slots);

        return hasIngredient && hasInput;
    }

    @Override
    public ResourceLocation id() {
        return BuiltInRegistries.POTION.getKey(getTo(recipe));
    }
}
