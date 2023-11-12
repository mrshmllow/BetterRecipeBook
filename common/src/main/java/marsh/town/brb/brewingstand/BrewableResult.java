package marsh.town.brb.brewingstand;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.generic.GenericRecipe;
import net.minecraft.core.RegistryAccess;
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

    public ItemStack inputAsItemStack(BRBBookCategories.Category category) {
        Potion inputPotion = getFrom(recipe);

        ResourceLocation identifier = BuiltInRegistries.POTION.getKey(inputPotion);
        ItemStack inputStack;
        if (category == BetterRecipeBook.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (category == BetterRecipeBook.BREWING_LINGERING_POTION) {
            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateTag().putString("Potion", identifier.toString());
        return inputStack;
    }

    public boolean hasInput(BRBBookCategories.Category category, List<Slot> slots) {
        ItemStack inputStack = inputAsItemStack(category);

        for (Slot slot : slots) {
            ItemStack itemStack = slot.getItem();

            if (inputStack.getTag() == null) return false;
            if (inputStack.getTag().equals(itemStack.getTag()) && inputStack.getItem().equals(itemStack.getItem()))
                return true;
        }

        return false;
    }

    public boolean hasMaterials(BRBBookCategories.Category category, List<Slot> slots) {
        boolean hasIngredient = hasIngredient(slots);
        boolean hasInput = hasInput(category, slots);

        return hasIngredient && hasInput;
    }

    @Override
    public ResourceLocation id() {
        return BuiltInRegistries.POTION.getKey(getTo(recipe));
    }

    @Override
    public ItemStack getResult(RegistryAccess registryAccess) {
        return ingredient;
    }

    @Override
    public String getSearchString() {
        return this.ingredient.getHoverName().getString();
    }
}
