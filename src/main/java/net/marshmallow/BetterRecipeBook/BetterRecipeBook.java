package net.marshmallow.BetterRecipeBook;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.marshmallow.BetterRecipeBook.Config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class BetterRecipeBook implements ModInitializer {
    public static int queuedScroll;
    public static boolean isHoldingShift;
    public static boolean hasWarnedNoPermission;
    public static boolean isFilteringNone;

    public static Config config;

    @Override
    public void onInitialize() {
        queuedScroll = 0;
        hasWarnedNoPermission = false;
        isFilteringNone = true;

        AutoConfig.register(Config.class, Toml4jConfigSerializer::new);

        config = AutoConfig.getConfigHolder(Config.class).getConfig();
    }

    }
}
