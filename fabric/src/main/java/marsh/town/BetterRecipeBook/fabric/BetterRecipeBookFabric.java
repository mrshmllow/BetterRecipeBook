package marsh.town.BetterRecipeBook.fabric;

import marsh.town.BetterRecipeBook.BetterRecipeBook;
import net.fabricmc.api.ModInitializer;

public class BetterRecipeBookFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BetterRecipeBook.init();
    }
}
