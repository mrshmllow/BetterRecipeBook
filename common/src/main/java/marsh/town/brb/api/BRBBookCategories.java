package marsh.town.brb.api;

import com.google.common.collect.ImmutableList;
import marsh.town.brb.util.BRBHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BRBBookCategories {
    public static Map<BRBHelper.Book, List<Category>> categories = new HashMap<>();

    @Nullable
    public static List<Category> getCategories(BRBHelper.Book book) {
        return categories.get(book);
    }

    private static Category createCategory(BRBHelper.Book book, Category.Type type, ItemStack... entries) {
        Category category = new Category(type, entries);
        categories.putIfAbsent(book, new ArrayList<>());

        categories.get(book).add(category);

        return category;
    }

    public static Category createCategory(BRBHelper.Book book, @NotNull ItemStack... entries) {
        return createCategory(book, Category.Type.OTHER, entries);
    }

    public static Category createSearch(BRBHelper.Book book) {
        return createCategory(book, Category.Type.SEARCH, new ItemStack(Items.COMPASS));
    }

    public static class Category {
        private final List<ItemStack> itemIcons;
        private final Type type;

        Category(Type type, ItemStack... entries) {
            this.itemIcons = ImmutableList.copyOf(entries);
            this.type = type;
        }

        public List<ItemStack> getItemIcons() {
            return this.itemIcons;
        }

        public Type getType() {
            return this.type;
        }

        public enum Type {
            SEARCH,
            OTHER
        }
    }
}
