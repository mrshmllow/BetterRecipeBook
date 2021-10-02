package net.marshmallow.BetterRecipeBook;

import net.marshmallow.BetterRecipeBook.Mixins.Accessors.BrewingRecipeRegistryRecipeAccessor;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class PinnedRecipeManager {
    public List<Identifier> pinned;

    public PinnedRecipeManager(List<Identifier> pinned) {
        this.pinned = pinned;
    }

    private boolean recipeResultCollectionToString(RecipeResultCollection target) {
        for (Recipe<?> recipe : target.getAllRecipes()) {
            recipe.getId();
        }

        return new RecipeResultCollection(target.getAllRecipes()).getAllRecipes().equals(target.getAllRecipes());
    }

    public void addOrRemoveFavourite(RecipeResultCollection target) {
        recipeResultCollectionToString(target);

        for (Identifier identifier : this.pinned) {
            for (Recipe<?> recipe : target.getAllRecipes()) {
                if (recipe.getId().equals(identifier)) {
                    this.pinned.remove(identifier);
                    return;
                }
            }
        }

        this.pinned.add(target.getAllRecipes().get(0).getId());
    }

    public void addOrRemoveFavouritePotion(BrewingRecipeRegistry.Recipe target) {
        Identifier targetIdentifier = Registry.POTION.getId(((BrewingRecipeRegistryRecipeAccessor<Potion>) target).getOutput());

        for (Identifier identifier : this.pinned) {
            if (identifier.equals(targetIdentifier)) {
                this.pinned.remove(targetIdentifier);
                return;
            }
        }

        this.pinned.add(targetIdentifier);
    }

    public boolean has(Object target) {
        for (Identifier identifier : this.pinned) {
            for (Recipe<?> recipe : ((RecipeResultCollection) target).getAllRecipes()) {
                if (recipe.getId().equals(identifier)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasPotion(BrewingRecipeRegistry.Recipe target) {
        Identifier targetIdentifier = Registry.POTION.getId(((BrewingRecipeRegistryRecipeAccessor<Potion>) target).getOutput());

        for (Identifier identifier : this.pinned) {
            if (targetIdentifier.equals(identifier)) {
                return true;
            }
        }
        return false;
    }
}
