package marsh.town.BetterRecipeBook.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import marsh.town.BetterRecipeBook.Config.Config;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuFabric implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(Config.class, parent).get();
    }
}
