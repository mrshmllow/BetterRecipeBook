package marsh.town.brb.recipe.smithing;

import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.mixins.accessors.smithing.SmithingTrimRecipeAccessor;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

import java.util.ArrayList;
import java.util.Optional;

public class BRBSmithingTrimRecipe extends SmithingTrimRecipe implements BRBSmithingRecipe {
    private final ItemStack itemStackBase;

    public BRBSmithingTrimRecipe(Ingredient template, Ingredient base, ItemStack itemStackBase, Ingredient addition) {
        super(template, base, addition);
        this.itemStackBase = itemStackBase;
    }

    public static ArrayList<BRBSmithingTrimRecipe> from(SmithingTrimRecipe recipe) {
        SmithingTrimRecipeAccessor recipeAccessor = (SmithingTrimRecipeAccessor) recipe;
        ArrayList<BRBSmithingTrimRecipe> results = new ArrayList<>();

        for (ItemStack base : recipeAccessor.getUnderlyingBase().getItems()) {
            results.add(new BRBSmithingTrimRecipe(recipeAccessor.getUnderlyingTemplate(), recipeAccessor.getUnderlyingBase(), base, recipeAccessor.getUnderlyingAddition()));
        }

        return results;
    }

    @Override
    public ItemStack getResult(RegistryAccess registryAccess, BRBBookCategories.Category category) {
        return this.getResult(TrimMaterials.REDSTONE, registryAccess, category);
    }

    @Override
    public ItemStack getResult(ResourceKey<TrimMaterial> trimMaterialResourceKey, RegistryAccess registryAccess, BRBBookCategories.Category category) {
        Optional<Holder.Reference<TrimMaterial>> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolder(trimMaterialResourceKey);
        Optional<Holder.Reference<TrimPattern>> trim = TrimPatterns.getFromTemplate(registryAccess, this.getTemplate().getItems()[0]);

        ItemStack itemStack = this.itemStackBase.copy();

        if (material.isPresent() && trim.isPresent()) {
            ArmorTrim armorTri = new ArmorTrim(material.get(), trim.get());
            itemStack.set(DataComponents.TRIM, armorTri);
        }

        return itemStack;
    }

    @Override
    public ItemStack getBase() {
        return this.itemStackBase;
    }

    @Override
    public Ingredient getTemplate() {
        return ((SmithingTrimRecipeAccessor) this).getUnderlyingTemplate();
    }

    @Override
    public Ingredient getAddition() {
        return ((SmithingTrimRecipeAccessor) this).getUnderlyingAddition();
    }
}
