package marsh.town.BetterRecipeBook.Config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = "betterrecipebook")
@me.shedaniel.autoconfig.annotation.Config.Gui.Background("minecraft:textures/block/blue_concrete_powder.png")
public class Config implements ConfigData {
    public boolean enableBook = true;

    public boolean enablePinning = true;

    @ConfigEntry.Gui.Tooltip()
    public boolean settingsButton = true;

    @ConfigEntry.Gui.Tooltip()
    public boolean darkMode = false;

    public boolean keepCentered = true;

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
}
