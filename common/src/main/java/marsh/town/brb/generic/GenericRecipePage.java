package marsh.town.brb.generic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface GenericRecipePage<M extends AbstractContainerMenu> {
    void initialize(Minecraft client, int parentLeft, int parentTop, M smithingMenuHandler, int leftOffset);

    boolean mouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m);

    void drawTooltip(GuiGraphics gui, int mouseX, int mouseY);

    boolean overlayIsVisible();

    boolean isFilteringCraftable();
}
