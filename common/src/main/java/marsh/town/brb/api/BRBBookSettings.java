package marsh.town.brb.api;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.mixins.accessors.BookSettingsTypeSettingsAccessor;
import marsh.town.brb.util.BRBHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBookSettings;

import java.util.HashMap;
import java.util.Map;

public class BRBBookSettings {
    public static Map<ResourceLocation, RecipeBookSettings.TypeSettings> states = new HashMap<>();

    public BRBBookSettings() {
        states = new HashMap<>();
    }

    public static void registerBook(BRBHelper.Book book) {
        BetterRecipeBook.LOGGER.info("Registering book {}", book.resourceLocation);
        states.put(book.resourceLocation, new RecipeBookSettings.TypeSettings(false, false));
    }

    public static boolean isOpen(BRBHelper.Book book) {
        RecipeBookSettings.TypeSettings settings = states.get(book.resourceLocation);

        return ((BookSettingsTypeSettingsAccessor) settings).isOpen();
    }

    public static void setOpen(BRBHelper.Book book, boolean bl) {
        ((BookSettingsTypeSettingsAccessor) states.get(book.resourceLocation)).setOpen(bl);
    }

    public static boolean isFiltering(BRBHelper.Book book) {
        return ((BookSettingsTypeSettingsAccessor) states.get(book.resourceLocation)).isFiltering();
    }

    public static void setFiltering(BRBHelper.Book book, boolean bl) {
        ((BookSettingsTypeSettingsAccessor) states.get(book.resourceLocation)).setFiltering(bl);
    }

    public int hashCode() {
        return states.hashCode();
    }
}
