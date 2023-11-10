package marsh.town.brb.generic;

import marsh.town.brb.recipe.BRBRecipeBookCategories;
import net.minecraft.client.gui.components.StateSwitchingButton;

public abstract class GenericRecipeGroupButtonWidget extends StateSwitchingButton {
    public GenericRecipeGroupButtonWidget(int i, int j, int k, int l, boolean bl) {
        super(i, j, k, l, bl);
    }

    protected void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected abstract BRBRecipeBookCategories getGroup();
}
