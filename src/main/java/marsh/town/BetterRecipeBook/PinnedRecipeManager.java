package marsh.town.BetterRecipeBook;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import marsh.town.BetterRecipeBook.Mixins.Accessors.BrewingRecipeRegistryRecipeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PinnedRecipeManager {
    public List<ResourceLocation> pinned;

    public void read() {
        Gson gson = new Gson();
        JsonReader reader = null;

        try {
            File pinsFile = new File(Minecraft.getInstance().gameDirectory, "brb.pins");

            if (pinsFile.exists()) {
                reader = new JsonReader(new FileReader(pinsFile.getAbsolutePath()));
                Type type = new TypeToken<List<ResourceLocation>>() {}.getType();
                pinned = gson.fromJson(reader, type);
            }
        } catch (Throwable var8) {
            BetterRecipeBook.LOGGER.error("brb.pins could not be read.");
        } finally {
            if (pinned == null) {
                pinned = new ArrayList<>();
            }
            IOUtils.closeQuietly(reader);
        }
    }

    private void store() {
        Gson gson = new Gson();
        OutputStreamWriter writer = null;

        try {
            File pinsFile = new File(Minecraft.getInstance().gameDirectory, "brb.pins");
            writer = new OutputStreamWriter(new FileOutputStream(pinsFile), StandardCharsets.UTF_8);
            writer.write(gson.toJson(this.pinned));
        } catch (Throwable var8) {
            BetterRecipeBook.LOGGER.error("brb.pins could not be saved.");
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public void addOrRemoveFavourite(RecipeCollection target) {
        for (ResourceLocation identifier : this.pinned) {
            for (Recipe<?> recipe : target.getRecipes()) {
                if (recipe.getId().equals(identifier)) {
                    this.pinned.remove(identifier);
                    this.store();
                    return;
                }
            }
        }

        this.pinned.add(target.getRecipes().get(0).getId());
        this.store();
    }

    public void addOrRemoveFavouritePotion(PotionBrewing.Mix<?> target) {
        ResourceLocation targetIdentifier = Registry.POTION.getKey((Potion) ((BrewingRecipeRegistryRecipeAccessor<?>) target).getTo());

        for (ResourceLocation identifier : this.pinned) {
            if (identifier.equals(targetIdentifier)) {
                this.pinned.remove(targetIdentifier);
                this.store();
                return;
            }
        }

        this.pinned.add(targetIdentifier);
        this.store();
    }

    public boolean has(Object target) {
        for (ResourceLocation identifier : this.pinned) {
            for (Recipe<?> recipe : ((RecipeCollection) target).getRecipes()) {
                if (recipe.getId().equals(identifier)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasPotion(PotionBrewing.Mix<?> target) {
        ResourceLocation targetIdentifier = Registry.POTION.getKey((Potion) ((BrewingRecipeRegistryRecipeAccessor<?>) target).getTo());

        for (ResourceLocation identifier : this.pinned) {
            if (targetIdentifier.equals(identifier)) {
                return true;
            }
        }
        return false;
    }
}
