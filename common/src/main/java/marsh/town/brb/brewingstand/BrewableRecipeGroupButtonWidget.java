package marsh.town.brb.brewingstand;

import com.mojang.blaze3d.systems.RenderSystem;
import marsh.town.brb.util.BRBTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BrewableRecipeGroupButtonWidget extends StateSwitchingButton {
    private final BrewingRecipeBookGroup group;

    public BrewableRecipeGroupButtonWidget(BrewingRecipeBookGroup category) {
        super(0, 0, 35, 27, false);
        this.initTextureValues(BRBTextures.RECIPE_BOOK_TAB_SPRITES);
        this.group = category;
    }

    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        Minecraft minecraftClient = Minecraft.getInstance();

        ResourceLocation sprite = this.sprites.get(true, this.isStateTriggered);
        int x = getX();
        if (this.isStateTriggered) {
            x -= 2;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        gui.blitSprite(sprite, x, this.getY(), this.width, this.height);
        RenderSystem.enableDepthTest();

        this.renderIcons(gui, minecraftClient.getItemRenderer());
    }

    private void renderIcons(GuiGraphics guiGraphics, ItemRenderer itemRenderer) {
        List<ItemStack> list = this.group.getIcons();
        int i = this.isStateTriggered ? -2 : 0;
        if (list.size() == 1) {
            guiGraphics.renderFakeItem(list.get(0), getX() + 9 + i, getY() + 5);
        } else if (list.size() == 2) {
            guiGraphics.renderFakeItem(list.get(0), getX() + 3 + i, getY() + 5);
            guiGraphics.renderFakeItem(list.get(1), getX() + 14 + i, getY() + 5);
        }

    }

    public BrewingRecipeBookGroup getGroup() {
        return this.group;
    }
}
