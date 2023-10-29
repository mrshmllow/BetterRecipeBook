package marsh.town.brb;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getTo;

public class PinnedRecipeManager {
    public HashSet<ResourceLocation> pinned;

    public void read() {
        Gson gson = new Gson();
        JsonReader reader = null;

        try {
            File pinsFile = new File(Minecraft.getInstance().gameDirectory, BetterRecipeBook.MOD_ID + ".pins");

            if (pinsFile.exists()) {
                reader = new JsonReader(new FileReader(pinsFile.getAbsolutePath()));
                Type type = new TypeToken<HashSet<ResourceLocation>>() {}.getType();
                pinned = gson.fromJson(reader, type);
            }
        } catch (Throwable var8) {
            BetterRecipeBook.LOGGER.error(BetterRecipeBook.MOD_ID + ".pins could not be read.");
        } finally {
            if (pinned == null) {
                pinned = new HashSet<>();
            }
            IOUtils.closeQuietly(reader);
        }
    }

    private void store() {
        Gson gson = new Gson();
        OutputStreamWriter writer = null;

        try {
            File pinsFile = new File(Minecraft.getInstance().gameDirectory, BetterRecipeBook.MOD_ID + ".pins");
            writer = new OutputStreamWriter(new FileOutputStream(pinsFile), StandardCharsets.UTF_8);
            writer.write(gson.toJson(this.pinned));
        } catch (Throwable var8) {
            BetterRecipeBook.LOGGER.error(BetterRecipeBook.MOD_ID + ".pins could not be saved.");
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public void addOrRemoveFavourite(RecipeCollection target) {
        for (ResourceLocation identifier : this.pinned) {
            for (RecipeHolder<?> recipe : target.getRecipes()) {
                if (recipe.id().equals(identifier)) {
                    this.pinned.remove(identifier);
                    this.store();
                    return;
                }
            }
        }

        this.pinned.addAll(target.getRecipes().stream().map(RecipeHolder::id).toList());
        this.store();
    }

    public void addOrRemoveFavouritePotion(PotionBrewing.Mix<?> target) {
        ResourceLocation targetIdentifier = BuiltInRegistries.POTION.getKey(getTo(target));

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

    public void addOrRemoveFavouriteSmithing(SmithingRecipe recipe) {
//        ResourceLocation targetIdentifier = BuiltInRegistries.POTION.getKey(getTo(target));

//        for (ResourceLocation identifier : this.pinned) {
//            if (identifier.equals(targetIdentifier)) {
//                this.pinned.remove(targetIdentifier);
//                this.store();
//                return;
//            }
//        }
//
//        this.pinned.add(targetIdentifier);
        this.store();
    }

    public boolean has(Object target) {
        for (ResourceLocation identifier : this.pinned) {
            for (RecipeHolder<?> recipe : ((RecipeCollection) target).getRecipes()) {
                if (recipe.id().equals(identifier)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasPotion(PotionBrewing.Mix<?> target) {
        ResourceLocation targetIdentifier = BuiltInRegistries.POTION.getKey(getTo(target));

        for (ResourceLocation identifier : this.pinned) {
            if (targetIdentifier.equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    public static void handlePinRecipe(RecipeBookComponent book, RecipeBookPage page, RecipeHolder<?> recipe) {
        RecipeCollection collection = new RecipeCollection(Minecraft.getInstance().level.registryAccess(), List.of(recipe));
        collection.updateKnownRecipes(page.getRecipeBook());
        BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavourite(collection);
        ((RecipeBookComponentAccessor) book).updateCollectionsInvoker(false);
    }

}
