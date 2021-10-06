package net.marshmallow.BetterRecipeBook.BrewingStand;

import net.marshmallow.BetterRecipeBook.Mixins.Accessors.BrewingRecipeRegistryRecipeAccessor;
import net.marshmallow.BetterRecipeBook.Mixins.Accessors.PlayerInventoryAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
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

    public boolean hasIngredient() {
        for (ItemStack itemStack : ((BrewingRecipeRegistryRecipeAccessor<?>) this.recipe).getIngredient().getMatchingStacks()) {
            assert MinecraftClient.getInstance().player != null;
            if (MinecraftClient.getInstance().player.getInventory().contains(itemStack)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasInput(BrewingRecipeBookGroup group) {
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

        assert MinecraftClient.getInstance().player != null;
        for (DefaultedList<ItemStack> itemStacks : ((PlayerInventoryAccessor) MinecraftClient.getInstance().player.getInventory()).getCombinedInventory()) {
            for (ItemStack o : itemStacks) {
                if (inputStack.getNbt() == null) return false;

                if (inputStack.getNbt().equals(o.getNbt()) && inputStack.getItem().equals(o.getItem())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasMaterials(BrewingRecipeBookGroup group) {
        boolean hasIngredient = hasIngredient();
        boolean hasInput = hasInput(group);

        return hasIngredient && hasInput;
    }
}
