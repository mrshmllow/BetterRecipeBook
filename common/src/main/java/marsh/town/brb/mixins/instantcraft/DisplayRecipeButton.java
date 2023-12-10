package marsh.town.brb.mixins.instantcraft;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DisplayRecipeButton extends RecipeButton {

    protected ItemStack display;
    protected ResourceLocation backgroundSprite;

    public DisplayRecipeButton(ItemStack display, ResourceLocation backgroundSprite) {
        this.display = display;
        this.backgroundSprite = backgroundSprite;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.blitSprite(backgroundSprite, this.getX(), this.getY(), this.width, this.height);
        guiGraphics.renderFakeItem(display, this.getX() + 4, this.getY() + 4);
    }

}
