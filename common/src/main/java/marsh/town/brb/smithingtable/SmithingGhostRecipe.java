package marsh.town.brb.smithingtable;

import com.google.common.collect.Lists;
import marsh.town.brb.mixins.accessors.HolderReferenceAccessor;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import marsh.town.brb.recipe.smithing.BRBSmithingTransformRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.*;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SmithingGhostRecipe {
    private final Consumer<SmithingGhostRecipe> onGhostUpdate;
    @Nullable
    private BRBSmithingRecipe recipe;
    private final List<SmithingGhostIngredient> ingredients = Lists.newArrayList();
    float time;
    private final RegistryAccess registryAccess;

    public SmithingGhostRecipe(Consumer<SmithingGhostRecipe> onGhostUpdate, RegistryAccess registryAccess) {
        this.onGhostUpdate = onGhostUpdate;
        this.registryAccess = registryAccess;
    }

    public void clear() {
        this.recipe = null;
        this.ingredients.clear();
        this.time = 0.0F;
    }

    public void addIngredient(Ingredient ingredient, int i, int j) {
        this.ingredients.add(new SmithingGhostIngredient(ingredient, i, j));
    }

    public SmithingGhostIngredient get(int i) {
        return this.ingredients.get(i);
    }

    public int size() {
        return this.ingredients.size();
    }

    @Nullable
    public BRBSmithingRecipe getRecipe() {
        return this.recipe;
    }

    public ItemStack getCurrentResult() {
        if (this.recipe == null) {
            return ItemStack.EMPTY;
        }

        if (this.recipe instanceof BRBSmithingTransformRecipe) {
            return this.recipe.getResult(registryAccess);
        }

        ItemStack itemStack = this.recipe.getBase().copy();

        Stream<Holder.Reference<TrimMaterial>> holders = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).holders();

        Optional<Holder.Reference<TrimMaterial>> currentMaterialReference = TrimMaterials.getFromIngredient(registryAccess, this.ingredients.get(0).getItem());

        if (currentMaterialReference.isEmpty()) {
            return itemStack;
        }

        Holder.Reference<TrimMaterial> material = holders.filter(holder -> ((HolderReferenceAccessor<TrimMaterial>) holder).getKey().equals(((HolderReferenceAccessor<TrimMaterial>) currentMaterialReference.get()).getKey())).findFirst().get();

        Optional<Holder.Reference<TrimPattern>> trim = TrimPatterns.getFromTemplate(registryAccess, recipe.getTemplate().getItems()[0]);

        if (trim.isPresent()) {
            ArmorTrim armorTri = new ArmorTrim(material, trim.get());
            ArmorTrim.setTrim(registryAccess, itemStack, armorTri);
        }

        return itemStack;
    }

    public void setRecipe(@Nullable BRBSmithingRecipe recipe) {
        this.recipe = recipe;
    }

    public void render(GuiGraphics guiGraphics, Minecraft minecraft, int i, int j, boolean bl, float f) {
        if (!Screen.hasControlDown()) {
            this.time += f;
            this.onGhostUpdate.accept(this);
        }

        for (int k = 0; k < this.ingredients.size(); ++k) {
            SmithingGhostIngredient smithingGhostIngredient = this.ingredients.get(k);
            int l = smithingGhostIngredient.getX() + i;
            int m = smithingGhostIngredient.getY() + j;
            if (k == 0 && bl) {
                guiGraphics.fill(l - 4, m - 4, l + 20, m + 20, 822018048);
            } else {
                guiGraphics.fill(l, m, l + 16, m + 16, 822018048);
            }

            ItemStack itemStack = smithingGhostIngredient.getItem();
            guiGraphics.renderFakeItem(itemStack, l, m);
            guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), l, m, l + 16, m + 16, 822083583);
            if (k == 0) {
                guiGraphics.renderItemDecorations(minecraft.font, itemStack, l, m);
            }
        }

        ItemStack itemStack = getCurrentResult();

        if (itemStack.isEmpty()) return;

        int l = 98 + i;
        int m = SmithingMenu.SLOT_Y_PLACEMENT + j;
        if (bl) {
            guiGraphics.fill(l - 4, m - 4, l + 20, m + 20, 822018048);
        } else {
            guiGraphics.fill(l, m, l + 16, m + 16, 822018048);
        }

        guiGraphics.renderFakeItem(itemStack, l, m);
        guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), l, m, l + 16, m + 16, 822083583);
        guiGraphics.renderItemDecorations(minecraft.font, itemStack, l, m);
    }

    @Environment(EnvType.CLIENT)
    public class SmithingGhostIngredient {
        private final Ingredient ingredient;
        private final int x;
        private final int y;

        public SmithingGhostIngredient(Ingredient ingredient, int i, int j) {
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
            return itemStacks.length == 0 ? ItemStack.EMPTY : itemStacks[Mth.floor(SmithingGhostRecipe.this.time / 30.0F) % itemStacks.length];
        }
    }
}
