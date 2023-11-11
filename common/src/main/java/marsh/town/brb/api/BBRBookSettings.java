package marsh.town.brb.api;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.util.BRBHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BBRBookSettings {
    public static Map<ResourceLocation, TypeSettings> states = new HashMap<>();

    public BBRBookSettings() {
        states = new HashMap<>();
    }

    public static void registerBook(BRBHelper.Book book) {
        BetterRecipeBook.LOGGER.info("Registering book {}", book.resourceLocation);
        states.put(book.resourceLocation, new TypeSettings(false, false));
    }

    public static boolean isOpen(BRBHelper.Book book) {
        return states.get(book.resourceLocation).open;
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

    static final class TypeSettings {
        boolean open;
        boolean filtering;

        public TypeSettings(boolean bl, boolean bl2) {
            this.open = bl;
            this.filtering = bl2;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof TypeSettings) {
                TypeSettings typeSettings = (TypeSettings) object;
                return this.open == typeSettings.open && this.filtering == typeSettings.filtering;
            }
            return false;
        }

        public int hashCode() {
            int i = this.open ? 1 : 0;
            i = 31 * i + (this.filtering ? 1 : 0);
            return i;
        }

        public String toString() {
            return "[open=" + this.open + ", filtering=" + this.filtering + "]";
        }
    }

}
