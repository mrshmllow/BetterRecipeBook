package net.marshmallow.BetterRecipeBook;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.marshmallow.BetterRecipeBook.Config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetterRecipeBook implements ModInitializer {
    public static int queuedScroll;
    public static boolean hasWarnedNoPermission;
    public static boolean isFilteringNone;
    public static boolean inventorioLoaded;

    public static Config config;

    public static PinnedRecipeManager pinnedRecipeManager;
    public static InstantCraftingManager instantCraftingManager;

    public static boolean rememberedBrewingOpen = true;
    public static boolean rememberedBrewingToggle = false;

    public static final Logger LOGGER = LogManager.getLogger("betterrecipebook");

    @Override
    public void onInitialize() {
        queuedScroll = 0;
        hasWarnedNoPermission = false;
        isFilteringNone = true;

        AutoConfig.register(Config.class, Toml4jConfigSerializer::new);

        config = AutoConfig.getConfigHolder(Config.class).getConfig();

        pinnedRecipeManager = new PinnedRecipeManager();
        pinnedRecipeManager.read();
        instantCraftingManager = new InstantCraftingManager(config.instantCraft.instantCraft);

        inventorioLoaded = FabricLoader.getInstance().isModLoaded("inventorio");
    }
}
