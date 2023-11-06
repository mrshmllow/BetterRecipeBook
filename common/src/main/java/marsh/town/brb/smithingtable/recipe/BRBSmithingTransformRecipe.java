package marsh.town.brb.smithingtable.recipe;

import marsh.town.brb.mixins.accessors.smithing.SmithingTransformRecipeAccessor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class BRBSmithingTransformRecipe extends SmithingTransformRecipe implements BRBSmithingRecipe {
    public BRBSmithingTransformRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        super(template, base, addition, result);
    }

    public static BRBSmithingTransformRecipe from(SmithingTransformRecipe recipe, RegistryAccess registryAccess) {
        SmithingTransformRecipeAccessor recipeAccessor = (SmithingTransformRecipeAccessor) recipe;
        return new BRBSmithingTransformRecipe(recipeAccessor.getUnderlyingTemplate(), recipeAccessor.getUnderlyingBase(), recipeAccessor.getUnderlyingAddition(), recipe.getResultItem(registryAccess));
    }

    @Override
    public ItemStack getResult(RegistryAccess registryAccess) {
        return ((SmithingTransformRecipeAccessor) this).getResult();
    }

    @Override
    public ItemStack getResult(ResourceKey<TrimMaterial> trimMaterialResourceKey, RegistryAccess registryAccess) {
        return getResult(registryAccess);
    }

    @Override
    public ItemStack getBase() {
        return ((SmithingTransformRecipeAccessor) this).getUnderlyingBase().getItems()[0];
    }

    @Override
    public Ingredient getTemplate() {
        return ((SmithingTransformRecipeAccessor) this).getUnderlyingTemplate();
    }

    @Override
    public Ingredient getAddition() {
        return ((SmithingTransformRecipeAccessor) this).getUnderlyingAddition();
    }
}
