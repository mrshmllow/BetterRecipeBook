package marsh.town.BetterRecipeBook;

import marsh.town.BetterRecipeBook.Config.Config;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetterRecipeBook implements ClientModInitializer {
    public static int queuedScroll;
    public static boolean hasWarnedNoPermission;
    public static boolean isFilteringNone;

    public static Config config;

    public static PinnedRecipeManager pinnedRecipeManager;
    public static InstantCraftingManager instantCraftingManager;

    public static boolean rememberedBrewingOpen = true;
    public static boolean rememberedBrewingToggle = false;

    public static final Logger LOGGER = LogManager.getLogger("brb");

    @Override
    public void onInitializeClient() {
        queuedScroll = 0;
        hasWarnedNoPermission = false;
        isFilteringNone = true;

        AutoConfig.register(Config.class, Toml4jConfigSerializer::new);

        config = AutoConfig.getConfigHolder(Config.class).getConfig();

        pinnedRecipeManager = new PinnedRecipeManager();
        pinnedRecipeManager.read();
        instantCraftingManager = new InstantCraftingManager(config.instantCraft.instantCraft);
    }
}
