package net.marshmallow.BetterRecipeBook;

import java.util.List;

public class PinnedRecipeManager {
    public List<Object> pinned;

    public PinnedRecipeManager(List<Object> pinned) {
        this.pinned = pinned;
    }

    public void addOrRemoveFavourite(Object target) {
        for (Object favourite : this.pinned) {
            if (target.equals(favourite)) {
                this.pinned.remove(target);
                return;
            }
        }

        this.pinned.add(target);
    }

    public boolean has(Object target) {
        return this.pinned.contains(target);
        // for (Object favourite : this.pinned) {
        //     if (target.equals(favourite)) {
        //         return true;
        //     }
        // }
        // return false;
    }
}
