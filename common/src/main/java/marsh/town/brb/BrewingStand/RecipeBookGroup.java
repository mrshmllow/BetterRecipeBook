package marsh.town.brb.BrewingStand;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public enum RecipeBookGroup {
    BREWING_SEARCH(new ItemStack(Items.COMPASS)),
    BREWING_POTION(new ItemStack(Items.POTION)),
    BREWING_SPLASH_POTION(new ItemStack(Items.SPLASH_POTION)),
    BREWING_LINGERING_POTION(new ItemStack(Items.LINGERING_POTION));

    public static final RecipeBookGroup POTION = BREWING_POTION;
    public static final RecipeBookGroup SPLASH = BREWING_SPLASH_POTION;
    public static final RecipeBookGroup LINGERING = BREWING_LINGERING_POTION;
    private final List<ItemStack> icons;

    RecipeBookGroup(ItemStack... entries) {
        this.icons = ImmutableList.copyOf(entries);
    }

    public static List<RecipeBookGroup> getGroups() {
        return Arrays.asList(POTION, SPLASH, LINGERING);
    }

    public List<ItemStack> getIcons() {
        return this.icons;
    }
}
