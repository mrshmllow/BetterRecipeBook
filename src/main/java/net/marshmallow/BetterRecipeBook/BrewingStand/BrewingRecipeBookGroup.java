package net.marshmallow.BetterRecipeBook.BrewingStand;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public enum BrewingRecipeBookGroup {
    BREWING_SEARCH(new ItemStack(Items.COMPASS)),
    BREWING_POTION(new ItemStack(Items.POTION)),
    BREWING_SPLASH_POTION(new ItemStack(Items.SPLASH_POTION)),
    BREWING_LINGERING_POTION(new ItemStack(Items.LINGERING_POTION));

    public static final BrewingRecipeBookGroup POTION = BREWING_POTION;
    public static final BrewingRecipeBookGroup SPLASH = BREWING_SPLASH_POTION;
    public static final BrewingRecipeBookGroup LINGERING = BREWING_LINGERING_POTION;
    private final List<ItemStack> icons;

    BrewingRecipeBookGroup(ItemStack... entries) {
        this.icons = ImmutableList.copyOf(entries);
    }

    public static List<BrewingRecipeBookGroup> getGroups() {
        return Arrays.asList(POTION, SPLASH, LINGERING);
    }

    public List<ItemStack> getIcons() {
        return this.icons;
    }
}
