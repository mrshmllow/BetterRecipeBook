package marsh.town.brb.api;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.util.BRBHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BRBBookSettings {
    public static Map<ResourceLocation, TypeSettings> states = new HashMap<>();

    public BRBBookSettings() {
        states = new HashMap<>();
    }

    public static void registerBook(BRBHelper.Book book) {
        BetterRecipeBook.LOGGER.info("Registering book {}", book.resourceLocation);
        states.put(book.resourceLocation, new TypeSettings(false, false));
    }

    public static boolean isOpen(BRBHelper.Book book) {
        TypeSettings settings = states.get(book.resourceLocation);

        return settings.open;
    }

    public static void setOpen(BRBHelper.Book book, boolean bl) {
        states.get(book.resourceLocation).open = bl;
    }

    public static boolean isFiltering(BRBHelper.Book book) {
        return states.get(book.resourceLocation).filtering;
    }

    public static void setFiltering(BRBHelper.Book book, boolean bl) {
        states.get(book.resourceLocation).filtering = bl;
    }

    public int hashCode() {
        return states.hashCode();
    }

    static class TypeSettings {
        boolean open;
        boolean filtering;

        public TypeSettings(boolean bl, boolean bl2) {
            this.open = bl;
            this.filtering = bl2;
        }
    }
}
