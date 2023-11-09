package marsh.town.brb.smithingtable;

import com.google.common.collect.ImmutableList;
import marsh.town.brb.enums.BRBRecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public enum BRBRecipeBookCategories {
    SMITHING_SEARCH(new ItemStack(Items.COMPASS)),
    SMITHING_TRANSFORM(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)),
    SMITHING_TRIM(new ItemStack(Items.NETHERITE_CHESTPLATE));

    public static final List<BRBRecipeBookCategories> SMITHING_RECIPES = ImmutableList.of(SMITHING_SEARCH, SMITHING_TRIM, SMITHING_TRANSFORM);
    private final List<ItemStack> itemIcons;

    BRBRecipeBookCategories(ItemStack... entries) {
        this.itemIcons = ImmutableList.copyOf(entries);
    }

    public static List<BRBRecipeBookCategories> getGroups(BRBRecipeBookType recipeBookType) {
        return switch (recipeBookType) {
            default -> throw new IncompatibleClassChangeError();
            case SMITHING -> SMITHING_RECIPES;
        };
    }

    public List<ItemStack> getItemIcons() {
        return this.itemIcons;
    }
}
