package net.marshmallow.BetterRecipeBook.BrewingStand;

import net.marshmallow.BetterRecipeBook.Mixins.Accessors.BrewingRecipeRegistryRecipeAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BrewingResult {
    public ItemStack itemStack;
    public BrewingRecipeRegistry.Recipe<?> recipe;
    public Identifier input;
    
    public BrewingResult (ItemStack itemStack, BrewingRecipeRegistry.Recipe<?> recipe) {
        this.itemStack = itemStack;
        this.recipe = recipe;
        this.input = Registry.POTION.getId((Potion) ((BrewingRecipeRegistryRecipeAccessor<?>) recipe).getInput());
    }

    public boolean hasIngredient(BrewingStandScreenHandler handledScreen) {
        for (ItemStack itemStack : ((BrewingRecipeRegistryRecipeAccessor<?>) this.recipe).getIngredient().getMatchingStacks()) {
            for (Slot slot : handledScreen.slots) {
                if (itemStack.getItem().equals(slot.getStack().getItem())) return true;
            }
        }
        return false;
    }

    public boolean hasInput(BrewingRecipeBookGroup group, BrewingStandScreenHandler handledScreen) {
        Potion inputPotion = (Potion) ((BrewingRecipeRegistryRecipeAccessor<?>) this.recipe).getInput();

        Identifier identifier = Registry.POTION.getId(inputPotion);
        ItemStack inputStack;
        if (group == BrewingRecipeBookGroup.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (group == BrewingRecipeBookGroup.BREWING_LINGERING_POTION) {
            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateNbt().putString("Potion", identifier.toString());

        for (Slot slot : handledScreen.slots) {
            ItemStack itemStack = slot.getStack();

            if (inputStack.getNbt() == null) return false;
            if (inputStack.getNbt().equals(itemStack.getNbt()) && inputStack.getItem().equals(itemStack.getItem())) return true;
        }

        return false;
    }

    public boolean hasMaterials(BrewingRecipeBookGroup group, BrewingStandScreenHandler handledScreen) {
        boolean hasIngredient = hasIngredient(handledScreen);
        boolean hasInput = hasInput(group, handledScreen);

        return hasIngredient && hasInput;
    }
}
