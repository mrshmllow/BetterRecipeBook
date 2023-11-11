package marsh.town.brb.util;

import com.mojang.datafixers.util.Pair;
import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.api.BRBBookSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BRBHelper {
    public static Book createBook(String mod_id, String name) {
        ResourceLocation location = new ResourceLocation(mod_id, name);

        String hash = location + "#";
        Pair<String, String> pair = new Pair<>(hash + "isGuiOpen", hash + "isFiltering");

        Book book = new Book(
                location,
                pair
        );

        BRBBookSettings.registerBook(book);

        return book;
    }

    static public class Book {
        public ResourceLocation resourceLocation;
        public Pair<String, String> pair;

        Book(ResourceLocation resourceLocation, Pair<String, String> pair) {
            this.resourceLocation = resourceLocation;
            this.pair = pair;
        }

        public BRBBookCategories.Category createCategory(ItemStack... entries) {
            return BRBBookCategories.createCategory(this, entries);
        }

        public BRBBookCategories.Category createSearch() {
            return BRBBookCategories.createSearch(this);
        }
    }
}
