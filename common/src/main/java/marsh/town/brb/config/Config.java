package marsh.town.brb.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = "brb")
@me.shedaniel.autoconfig.annotation.Config.Gui.Background("minecraft:textures/block/blue_concrete_powder.png")
public class Config implements ConfigData {
    public boolean enablePinning = true;

    @ConfigEntry.Gui.Tooltip()
    public boolean darkMode = false;

    public boolean keepCentered = false;

    @ConfigEntry.Category("newRecipes")
    @ConfigEntry.Gui.TransitiveObject()
    public NewRecipes newRecipes = new NewRecipes();

    @ConfigEntry.Category("instantCraft")
    @ConfigEntry.Gui.TransitiveObject()
    public InstantCraft instantCraft = new InstantCraft();

    @ConfigEntry.Category("alternativeRecipes")
    @ConfigEntry.Gui.TransitiveObject()
    public AlternativeRecipes alternativeRecipes = new AlternativeRecipes();

    @ConfigEntry.Category("scrolling")
    @ConfigEntry.Gui.TransitiveObject()
    public Scrolling scrolling = new Scrolling();

    @ConfigEntry.Gui.PrefixText()
    public boolean settingsButton = true;
    public boolean enableBook = true;
}
