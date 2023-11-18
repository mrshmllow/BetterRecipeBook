package marsh.town.brb.generic;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class GenericGhostRecipe<R extends GenericRecipe> {
    @Nullable
    protected Consumer<ItemStack> onGhostUpdate;
    @Nullable
    protected R recipe;
    protected final List<GenericGhostIngredient> ingredients = Lists.newArrayList();
    protected float time;
    protected RegistryAccess registryAccess;
    @Nullable
    private BiPredicate<GhostRenderType, GenericGhostIngredient> renderingPredicate;

    public GenericGhostRecipe(@Nullable Consumer<ItemStack> onGhostUpdate, RegistryAccess registryAccess) {
        this.onGhostUpdate = onGhostUpdate;
        this.registryAccess = registryAccess;
    }

    /**
     * @param renderingPredicate Returns true if {@link GhostRenderType} should be rendered
     */
    public void setRenderingPredicate(@Nullable BiPredicate<GhostRenderType, GenericGhostIngredient> renderingPredicate) {
        this.renderingPredicate = renderingPredicate;
    }

    public <T extends AbstractContainerMenu> void setDefaultRenderingPredicate(T menu) {
        this.setRenderingPredicate((type, ingredient) -> {
            ItemStack slot = menu.slots.get(ingredient.getContainerSlot()).getItem();
            switch (type) {
                case ITEM, BACKGROUND, TOOLTIP -> {
                    return slot.isEmpty();
                }
            }
            return true;
        });
    }

    public ItemStack getCurrentResult() {
        if (this.recipe == null) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = this.recipe.getResult(registryAccess);

        return itemStack.copy();
    }

    public void clear() {
        this.recipe = null;
        this.ingredients.clear();
        this.time = 0.0F;
    }

    public void addIngredient(int containerSlot, Ingredient ingredient, int i, int j) {
        this.ingredients.add(new GenericGhostIngredient(containerSlot, ingredient, i, j));
    }

    public GenericGhostIngredient get(int i) {
        return this.ingredients.get(i);
    }

    public int size() {
        return this.ingredients.size();
    }

    @Nullable
    public R getRecipe() {
        return this.recipe;
    }

    public void setRecipe(@Nullable R recipe) {
        this.recipe = recipe;
    }

    public void render(GuiGraphics guiGraphics, Minecraft minecraft, int i, int j, boolean bl, float f) {
        if (!Screen.hasControlDown()) {
            this.time += f;
            if (this.onGhostUpdate != null) this.onGhostUpdate.accept(this.getCurrentResult());
        }

        for (int k = 0; k < this.ingredients.size(); ++k) {
            GenericGhostIngredient ghostIngredient = this.ingredients.get(k);
            boolean shouldRenderBackground = renderingPredicate != null && renderingPredicate.test(GhostRenderType.BACKGROUND, ghostIngredient);
            boolean shouldRenderItem = renderingPredicate != null && renderingPredicate.test(GhostRenderType.ITEM, ghostIngredient);

            int l = ghostIngredient.getX() + i;
            int m = ghostIngredient.getY() + j;
            if (shouldRenderBackground) {
                if (k == 0 && bl) {
                    guiGraphics.fill(l - 4, m - 4, l + 20, m + 20, 0x30FF0000);
                } else {
                    guiGraphics.fill(l, m, l + 16, m + 16, 0x30FF0000);
                }
            }

            ItemStack itemStack = ghostIngredient.getItem();
            if (shouldRenderItem) {
                guiGraphics.renderFakeItem(itemStack, l, m);
            }

            if (shouldRenderBackground) {
                guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), l, m, l + 16, m + 16, 0x30FFFFFF);
            }

            if (k == 0) {
                guiGraphics.renderItemDecorations(minecraft.font, itemStack, l, m);
            }
        }
    }

    public GenericGhostIngredient getBySlot(int i) {
        for (GenericGhostIngredient ingredient : ingredients) {
            if (ingredient.getContainerSlot() == i) return ingredient;
        }
        return null;
    }

    public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
        ItemStack itemStack = null;

        for (GenericGhostIngredient ingredient : ingredients) {
            int j = ingredient.getX() + x;
            int k = ingredient.getY() + y;

            // don't render tooltip if cursor is not over item or predicate returns false
            if (mouseX >= j && mouseY >= k && mouseX < j + 16 && mouseY < k + 16 && (renderingPredicate == null || renderingPredicate.test(GhostRenderType.TOOLTIP, ingredient))) {
                itemStack = ingredient.getItem();
            }
        }

        if (itemStack != null && Minecraft.getInstance().screen != null) {
            gui.renderComponentTooltip(Minecraft.getInstance().font, Screen.getTooltipFromItem(Minecraft.getInstance(), itemStack), mouseX, mouseY);
        }
    }

    public class GenericGhostIngredient {
        private final Ingredient ingredient;
        private final int x;
        private final int y;
        private final int containerSlot;

        public GenericGhostIngredient(int containerSlot, Ingredient ingredient, int i, int j) {
            this.containerSlot = containerSlot;
            this.ingredient = ingredient;
            this.x = i;
            this.y = j;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public ItemStack getItem() {
            ItemStack[] itemStacks = this.ingredient.getItems();
            return itemStacks.length == 0 ? ItemStack.EMPTY : itemStacks[Mth.floor(GenericGhostRecipe.this.time / 30.0F) % itemStacks.length];
        }

        public int getContainerSlot() {
            return this.containerSlot;
        }

        public GenericGhostRecipe<R> getOwner() {
            return GenericGhostRecipe.this;
        }
    }

    public enum GhostRenderType {
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
