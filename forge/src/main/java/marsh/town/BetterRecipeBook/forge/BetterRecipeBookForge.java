package marsh.town.BetterRecipeBook.forge;

import marsh.town.BetterRecipeBook.BetterRecipeBook;
import net.minecraftforge.fml.common.Mod;

@Mod(BetterRecipeBook.MOD_ID)
public class BetterRecipeBookForge {
    public BetterRecipeBookForge() {
        BetterRecipeBook.init();
    }
}
