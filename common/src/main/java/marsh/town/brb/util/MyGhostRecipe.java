package marsh.town.brb.util;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.function.BiPredicate;

public class MyGhostRecipe extends GhostRecipe {

    private final List<MyGhostIngredient> ingredients = Lists.newArrayList();

    private BiPredicate<GhostRender, MyGhostIngredient> renderingPredicate = (a, b) -> true;

    public BiPredicate<GhostRender, MyGhostIngredient> getRenderingPredicate() {
        return renderingPredicate;
    }

    public MyGhostRecipe setRenderingPredicate(BiPredicate<GhostRender, MyGhostIngredient> renderingPredicate) {
        this.renderingPredicate = renderingPredicate;
        return this;
    }

    public void addIngredient(Ingredient ingredient, int x, int y) {
        this.ingredients.add(new MyGhostIngredient(this, ingredients.size(), ingredient, x, y));
    }

    public void addIngredient(int containerSlot, Ingredient ingredient, int x, int y) {
        this.ingredients.add(new MyGhostIngredient(this, containerSlot, ingredient, x, y));
    }

    public MyGhostIngredient get(int i) {
        return this.ingredients.get(i);
    }

    public MyGhostIngredient getBySlot(int i) {
        for (MyGhostIngredient ingredient : ingredients) {
            if (ingredient.getContainerSlot() == i) return ingredient;
        }
        return null;
    }

    public int size() {
        return this.ingredients.size();
    }

    public void render(GuiGraphics guiGraphics, Minecraft minecraft, int x, int y, boolean bl, float f) {
        for (int k = 0; k < this.ingredients.size(); ++k) {
            MyGhostIngredient ghostIngredient = this.ingredients.get(k);
            int tx = ghostIngredient.getX() + x;
            int ty = ghostIngredient.getY() + y;
            // don't render background if the predicate returns false
            boolean renderBackground = renderingPredicate.test(GhostRender.BACKGROUND, ghostIngredient);
            if (renderBackground) {
                if (k == 0 && bl) {
                    guiGraphics.fill(tx - 4, ty - 4, tx + 20, ty + 20, 0x30FF0000);
                } else {
                    guiGraphics.fill(tx, ty, tx + 16, ty + 16, 0x30FF0000);
                }
            }
            ItemStack itemStack = ghostIngredient.getItem();
            // don't render the itemstack if the predicate returns false
            boolean renderItem = renderingPredicate.test(GhostRender.ITEM, ghostIngredient);
            if (renderItem) {
                guiGraphics.renderFakeItem(itemStack, tx, ty);
            }
            if (renderBackground) {
                guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), tx, ty, tx + 16, ty + 16, 0x30FFFFFF);
            }
            if (k != 0 || !renderItem) continue;
            guiGraphics.renderItemDecorations(minecraft.font, itemStack, tx, ty);
        }
    }

    public void renderTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
        ItemStack itemStack = null;

        for (MyGhostIngredient ghostInputSlot : ingredients) {
            int j = ghostInputSlot.getX() + x;
            int k = ghostInputSlot.getY() + y;

            // don't render tooltip if cursor is not over item or predicate returns false
            if (mouseX >= j && mouseY >= k && mouseX < j + 16 && mouseY < k + 16 && renderingPredicate.test(GhostRender.TOOLTIP, ghostInputSlot)) {
                itemStack = ghostInputSlot.getItem();
            }
        }

        if (itemStack != null && Minecraft.getInstance().screen != null) {
            gui.renderComponentTooltip(Minecraft.getInstance().font, Screen.getTooltipFromItem(Minecraft.getInstance(), itemStack), mouseX, mouseY);
        }
    }

    public class MyGhostIngredient extends GhostIngredient {

        protected final MyGhostRecipe owner;
        protected final int containerSlot;

        public MyGhostIngredient(MyGhostRecipe owner, int containerSlot, Ingredient ingredient, int x, int z) {
            super(ingredient, x, z);
            this.owner = owner;
            this.containerSlot = containerSlot;
        }

        public MyGhostRecipe getOwner() {
            return owner;
        }

        public int getContainerSlot() {
            return containerSlot;
        }

    }

    public enum GhostRender {
        /**
         * When rendering the fake item model
         */
        ITEM,
        /**
         * When rendering the background color
         */
        BACKGROUND,
        /**
         * When rendering the fake item tooltip
         */
        TOOLTIP
    }

}
