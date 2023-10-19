package marsh.town.brb.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "scrolling")
public class Scrolling implements ConfigData {
    public boolean enableScrolling = true;

    @ConfigEntry.Gui.Tooltip()
    public boolean scrollAround = false;
}
