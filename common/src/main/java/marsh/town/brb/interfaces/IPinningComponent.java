package marsh.town.brb.interfaces;

import com.google.common.collect.Lists;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.generic.pins.Pinnable;

import java.util.List;

public interface IPinningComponent<T extends Pinnable> {
    default void betterRecipeBook$sortByPinsInPlace(List<T> results) {
        List<T> tempResults = Lists.newArrayList(results);

        if (BetterRecipeBook.config.enablePinning) {
            for (T result : tempResults) {
                if (BetterRecipeBook.pinnedRecipeManager.has(result)) {
                    results.remove(result);
                    results.add(0, result);
                }
            }
        }
    }
}
