package marsh.town.brb.BrewingStand;

public class BrewingRecipeBookGhostSlots {
/*    private final List<GhostSlot> slots = Lists.newArrayList();
    float time;

    public void reset() {
        this.slots.clear();
        this.time = 0.0F;
    }

    public void addSlot(ItemStack itemStack, int x, int y) {
        this.slots.add(new GhostSlot(itemStack, x, y));
    }

    public GhostSlot getSlot(int index) {
        return this.slots.get(index);
    }

    public int getSlotCount() {
        return this.slots.size();
    }

    public void draw(GuiGraphics guiGraphics, Minecraft minecraft, int i, int j, boolean bl, float f) {
        if (!Screen.hasControlDown()) {
            this.time += f;
        }
        for (int k = 0; k < this.slots.size(); ++k) {
            GhostRecipe.GhostIngredient ghostIngredient = this.slots.get(k);
            int l = ghostIngredient.getX() + i;
            int m = ghostIngredient.getY() + j;
            if (k == 0 && bl) {
                guiGraphics.fill(l - 4, m - 4, l + 20, m + 20, 0x30FF0000);
            } else {
                guiGraphics.fill(l, m, l + 16, m + 16, 0x30FF0000);
            }
            ItemStack itemStack = ghostIngredient.getItem();
            guiGraphics.renderFakeItem(itemStack, l, m);
            guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), l, m, l + 16, m + 16, 0x30FFFFFF);
            if (k != 0) continue;
            guiGraphics.renderItemDecorations(minecraft.font, itemStack, l, m);
        }

    }

    @Environment(EnvType.CLIENT)
    public static class GhostSlot {
        private final ItemStack itemStack;
        private final int x;
        private final int y;

        public GhostSlot(ItemStack itemStack, int x, int y) {
            this.itemStack = itemStack;
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public ItemStack getCurrentItemStack() {
            return itemStack;
        }
    }
*/
}
