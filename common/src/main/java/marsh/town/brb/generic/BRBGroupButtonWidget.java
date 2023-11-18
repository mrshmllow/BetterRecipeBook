package marsh.town.brb.generic;

import com.mojang.blaze3d.systems.RenderSystem;
import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.mixins.accessors.RecipeBookComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BRBGroupButtonWidget extends StateSwitchingButton {
    protected BRBBookCategories.Category category;

    public BRBGroupButtonWidget(BRBBookCategories.Category category) {
        super(0, 0, 35, 27, false);
        this.category = category;
        this.initTextureValues(153, 2, 35, 0, RecipeBookComponentAccessor.getRECIPE_BOOK_LOCATION());
    }

    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.disableDepthTest();
        int k = this.xTexStart;
        int l = this.yTexStart;
        if (this.isStateTriggered) {
            k += this.xDiffTex;
        }
        if (this.isHoveredOrFocused()) {
            l += this.yDiffTex;
        }
        int m = this.getX();
        if (this.isStateTriggered) {
            m -= 2;
        }
        gui.blit(this.resourceLocation, m, this.getY(), k, l, this.width, this.height);
        RenderSystem.enableDepthTest();
        this.renderIcons(gui, minecraft.getItemRenderer());
    }

    private void renderIcons(GuiGraphics guiGraphics, ItemRenderer itemRenderer) {
        List<ItemStack> list = this.category.getItemIcons();
        int i = this.isStateTriggered ? -2 : 0;
        if (list.size() == 1) {
            guiGraphics.renderFakeItem(list.get(0), getX() + 9 + i, getY() + 5);
        } else if (list.size() == 2) {
            guiGraphics.renderFakeItem(list.get(0), getX() + 3 + i, getY() + 5);
            guiGraphics.renderFakeItem(list.get(1), getX() + 14 + i, getY() + 5);
        }

    }

    public BRBBookCategories.Category getCategory() {
        return this.category;
    }
}
