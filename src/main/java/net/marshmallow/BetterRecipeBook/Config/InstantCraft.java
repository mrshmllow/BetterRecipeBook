package net.marshmallow.BetterRecipeBook.Config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name= "instantCraft")
public class InstantCraft implements ConfigData {
    public boolean instantCraft = false;
    public boolean showButton = true;
}
