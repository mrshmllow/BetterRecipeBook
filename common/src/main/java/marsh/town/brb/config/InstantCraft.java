package marsh.town.brb.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "instantCraft")
public class InstantCraft implements ConfigData {
    public boolean showButton = true;
    public boolean enabled = false;

}
