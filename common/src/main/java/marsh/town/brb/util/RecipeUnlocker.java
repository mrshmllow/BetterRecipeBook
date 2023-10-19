package marsh.town.brb.util;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.crafting.RecipeManager;

public class RecipeUnlocker {

    public static void unlockRecipesIfRequired() {
        if (BetterRecipeBook.config.newRecipes.unlockAll) {
            unlockRecipes();
        }
    }

    /**
     * Unlocks all recipes that the RecipeManager knows of, then updates any screen implementing RecipeUpdateListener
     */
    public static void unlockRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player != null && player.connection != null) {
            RecipeManager recipeManager = player.connection.getRecipeManager();
            ClientRecipeBook recipeBook = player.getRecipeBook();
            recipeManager.getRecipes().forEach(recipeBook::add);
            recipeBook.getCollections().forEach(recipeCollection -> recipeCollection.updateKnownRecipes(recipeBook));
            if (minecraft.screen instanceof RecipeUpdateListener rul) {
                rul.recipesUpdated();
            }
        }
    }

}
