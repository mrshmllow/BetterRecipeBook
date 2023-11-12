package marsh.town.brb.interfaces;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.config.Config;
import marsh.town.brb.util.BRBTextures;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public interface ISettingsButton {
    MutableComponent OPEN_SETTINGS_TOOLTIP = Component.translatable("brb.gui.settings.open");


    default ImageButton createSettingsButton(int i, int j) {
        if (BetterRecipeBook.config.settingsButton) {
            return new ImageButton(i + 11, j + 137, 18, 18, BRBTextures.SETTINGS_BUTTON_SPRITES, button -> {
                Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, Minecraft.getInstance().screen).get());
            });
        }
        return null;
    }

    default void renderSettingsButton(@Nullable ImageButton settingsButton, GuiGraphics gui, int mouseX, int mouseY, float delta) {
        if (settingsButton != null && BetterRecipeBook.config.settingsButton) {
            settingsButton.render(gui, mouseX, mouseY, delta);
        }
    }

    default boolean settingsButtonMouseClicked(@Nullable ImageButton settingsButton, double mouseX, double mouseY, int button) {
        if (settingsButton == null || !BetterRecipeBook.config.settingsButton) return false;

        return settingsButton.mouseClicked(mouseX, mouseY, button);
    }

    // TODO: Remove this and use .setTooltip and render it automatically
    default void renderSettingsButtonTooltip(@Nullable ImageButton settingsButton, GuiGraphics gui, int mouseX, int mouseY) {
        if (settingsButton != null && settingsButton.isHoveredOrFocused() && BetterRecipeBook.config.settingsButton
                && Minecraft.getInstance().screen != null) {
            gui.renderTooltip(Minecraft.getInstance().font, OPEN_SETTINGS_TOOLTIP, mouseX, mouseY);
        }
    }
}
