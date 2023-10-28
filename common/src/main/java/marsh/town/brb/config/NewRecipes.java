package marsh.town.brb.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "newRecipes")
public class NewRecipes {
    public boolean unlockAll = true;
    @ConfigEntry.Gui.Tooltip()
    public boolean enableBounce = false;
    public boolean forcePlaceRecipes = false;
}
