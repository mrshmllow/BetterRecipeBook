package marsh.town.brb.Config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "alternativeRecipes")
public class AlternativeRecipes {
    @ConfigEntry.Gui.Tooltip()
    public boolean onHover = true;
    public boolean noGrouped = false;
}
