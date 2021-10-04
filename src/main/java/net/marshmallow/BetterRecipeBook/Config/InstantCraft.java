package net.marshmallow.BetterRecipeBook.Config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name= "instant_craft_module")
public class InstantCraft implements ConfigData {
    public boolean instantCraft = false;
    public boolean showButton = true;
}
