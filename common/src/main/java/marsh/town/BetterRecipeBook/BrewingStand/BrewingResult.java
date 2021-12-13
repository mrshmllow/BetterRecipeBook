package marsh.town.BetterRecipeBook.BrewingStand;

import marsh.town.BetterRecipeBook.Mixins.Accessors.BrewingRecipeRegistryMixAccessor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

public class BrewingResult {
    public ItemStack ingredient;
    public PotionBrewing.Mix<?> recipe;
    public ResourceLocation input;
    
    public BrewingResult (ItemStack ingredient, PotionBrewing.Mix<?> recipe) {
        this.ingredient = ingredient;
        this.recipe = recipe;
        this.input = Registry.POTION.getKey((Potion) ((BrewingRecipeRegistryMixAccessor<?>) recipe).getFrom());
    }

    public boolean hasIngredient(BrewingStandMenu handledScreen) {
        for (ItemStack itemStack : ((BrewingRecipeRegistryMixAccessor<?>) this.recipe).getIngredient().getItems()) {
            for (Slot slot : handledScreen.slots) {
                if (itemStack.getItem().equals(slot.getItem().getItem())) return true;
            }
        }
        return false;
    }

    public ItemStack inputAsItemStack(BrewingRecipeBookGroup group) {
        Potion inputPotion = (Potion) ((BrewingRecipeRegistryMixAccessor<?>) this.recipe).getFrom();

        ResourceLocation identifier = Registry.POTION.getKey(inputPotion);
        ItemStack inputStack;
        if (group == BrewingRecipeBookGroup.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (group == BrewingRecipeBookGroup.BREWING_LINGERING_POTION) {
            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateTag().putString("Potion", identifier.toString());
        return inputStack;
    }

    public boolean hasInput(BrewingRecipeBookGroup group, BrewingStandMenu handledScreen) {
        Potion inputPotion = (Potion) ((BrewingRecipeRegistryMixAccessor<?>) this.recipe).getFrom();

        ItemStack inputStack = inputAsItemStack(group);

        for (Slot slot : handledScreen.slots) {
            ItemStack itemStack = slot.getItem();

            if (inputStack.getTag() == null) return false;
            if (inputStack.getTag().equals(itemStack.getTag()) && inputStack.getItem().equals(itemStack.getItem())) return true;
        }

        return false;
    }

    public boolean hasMaterials(BrewingRecipeBookGroup group, BrewingStandMenu handledScreen) {
        boolean hasIngredient = hasIngredient(handledScreen);
        boolean hasInput = hasInput(group, handledScreen);

        return hasIngredient && hasInput;
    }
}
