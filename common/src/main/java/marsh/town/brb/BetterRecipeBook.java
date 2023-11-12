package marsh.town.brb;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.config.Config;
import marsh.town.brb.loaders.PotionLoader;
import marsh.town.brb.util.BRBHelper;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetterRecipeBook {

    public static final String MOD_ID = "brb";

    public static int queuedScroll;
    public static boolean isFilteringNone;
    public static RecipeCollection currentHoveredRecipeCollection = null;

    public static Config config;

    public static PinnedRecipeManager pinnedRecipeManager;
    public static InstantCraftingManager instantCraftingManager;
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final KeyMapping PIN_MAPPING = new KeyMapping(
            "key.brb.pin",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_R,
            "category.brb"
    );

    public static BRBHelper.Book BREWING = BRBHelper.createBook(MOD_ID, "brewing_stand");
    public static BRBHelper.Book SMITHING = BRBHelper.createBook(MOD_ID, "smithing_table");

    public static BRBBookCategories.Category BREWING_POTION = BREWING.createCategory(new ItemStack(Items.POTION));
    public static BRBBookCategories.Category BREWING_SPLASH_POTION = BREWING.createCategory(new ItemStack(Items.SPLASH_POTION));
    public static BRBBookCategories.Category BREWING_LINGERING_POTION = BREWING.createCategory(new ItemStack(Items.LINGERING_POTION));
    public static BRBBookCategories.Category SMITHING_SEARCH = SMITHING.createSearch();
    public static BRBBookCategories.Category SMITHING_TRANSFORM = SMITHING.createCategory(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
    public static BRBBookCategories.Category SMITHING_TRIM = SMITHING.createCategory(new ItemStack(Items.NETHERITE_CHESTPLATE));

    public static void init() {
        PotionLoader.init();

        queuedScroll = 0;
        isFilteringNone = true;

        AutoConfig.register(Config.class, Toml4jConfigSerializer::new);

        config = AutoConfig.getConfigHolder(Config.class).getConfig();

        pinnedRecipeManager = new PinnedRecipeManager();
        pinnedRecipeManager.read();
        instantCraftingManager = new InstantCraftingManager();

        KeyMappingRegistry.register(PIN_MAPPING);
    }
}
