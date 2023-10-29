package marsh.town.brb.smithingtable;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;

public enum SmithingRecipeBookGroup {
    SMITHING_SEARCH(new ItemStack(Items.COMPASS)),
    SMITHING_TRANSFORM(new ItemStack(Items.NETHERITE_PICKAXE)),
    SMITHING_TRIM(new ItemStack(Items.NETHERITE_CHESTPLATE));

    public static final SmithingRecipeBookGroup TRANSFORM = SMITHING_TRANSFORM;
    public static final SmithingRecipeBookGroup TRIM = SMITHING_TRIM;
    private final List<ItemStack> icons;

    SmithingRecipeBookGroup(ItemStack... entries) {
        this.icons = ImmutableList.copyOf(entries);
    }

    public static List<SmithingRecipeBookGroup> getGroups() {
        return Arrays.asList(SMITHING_SEARCH, TRANSFORM, TRIM);
    }

    public List<ItemStack> getIcons() {
        return this.icons;
    }
}
