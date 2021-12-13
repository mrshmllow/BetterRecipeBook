package marsh.town.brb.fabric;

import marsh.town.brb.BetterRecipeBook;
import net.fabricmc.api.ModInitializer;

public class BetterRecipeBookFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BetterRecipeBook.init();
    }
}
