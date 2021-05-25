package net.marshmallow.BetterRecipeBook.Config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = "betterrecipebook")
public class Config implements ConfigData {
    public boolean enableBook = true;

    public boolean unlockAll = true;

    @ConfigEntry.Gui.Tooltip()
    public boolean enabledCheating = false;

    @ConfigEntry.Gui.Tooltip()
    public boolean enableBounce = false;

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public Scrolling scrollingModule = new Scrolling();

    @ConfigEntry.Gui.Tooltip()
    public boolean showAlternativesOnHover = true;
}
