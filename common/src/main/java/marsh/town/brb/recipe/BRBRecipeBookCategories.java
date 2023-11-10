package marsh.town.brb.recipe;

import com.google.common.collect.ImmutableList;
import marsh.town.brb.enums.BRBRecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public enum BRBRecipeBookCategories {
    SEARCH(new ItemStack(Items.COMPASS)),
    SMITHING_TRANSFORM(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)),
    SMITHING_TRIM(new ItemStack(Items.NETHERITE_CHESTPLATE)),
    BREWING_POTION(new ItemStack(Items.POTION)),
    BREWING_SPLASH_POTION(new ItemStack(Items.SPLASH_POTION)),
    BREWING_LINGERING_POTION(new ItemStack(Items.LINGERING_POTION));

    public static final List<BRBRecipeBookCategories> SMITHING_RECIPES = ImmutableList.of(SEARCH, SMITHING_TRIM, SMITHING_TRANSFORM);
    public static final List<BRBRecipeBookCategories> BREWING_RECIPES = ImmutableList.of(BREWING_POTION, BREWING_SPLASH_POTION, BREWING_LINGERING_POTION);
    private final List<ItemStack> itemIcons;

    BRBRecipeBookCategories(ItemStack... entries) {
        this.itemIcons = ImmutableList.copyOf(entries);
    }

    public static List<BRBRecipeBookCategories> getGroups(BRBRecipeBookType recipeBookType) {
        return switch (recipeBookType) {
            case SMITHING -> SMITHING_RECIPES;
            case BREWING -> BREWING_RECIPES;
        };
    }

    public List<ItemStack> getItemIcons() {
        return this.itemIcons;
    }
}
