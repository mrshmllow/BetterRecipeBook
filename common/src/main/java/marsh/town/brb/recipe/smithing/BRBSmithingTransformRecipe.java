package marsh.town.brb.recipe.smithing;

import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.mixins.accessors.smithing.SmithingTransformRecipeAccessor;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class BRBSmithingTransformRecipe extends SmithingTransformRecipe implements BRBSmithingRecipe {
    public BRBSmithingTransformRecipe(ResourceLocation resourceLocation, Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        super(resourceLocation, template, base, addition, result);
    }

    public static BRBSmithingTransformRecipe from(SmithingTransformRecipe recipe, RegistryAccess registryAccess) {
        SmithingTransformRecipeAccessor recipeAccessor = (SmithingTransformRecipeAccessor) recipe;
        return new BRBSmithingTransformRecipe(recipeAccessor.getId(), recipeAccessor.getUnderlyingTemplate(), recipeAccessor.getUnderlyingBase(), recipeAccessor.getUnderlyingAddition(), recipe.getResultItem(registryAccess));
    }

    @Override
    public ItemStack getResult(RegistryAccess registryAccess, BRBBookCategories.Category category) {
        return ((SmithingTransformRecipeAccessor) this).getResult();
    }

    @Override
    public ItemStack getResult(ResourceKey<TrimMaterial> trimMaterialResourceKey, RegistryAccess registryAccess, BRBBookCategories.Category category) {
        return getResult(registryAccess, category);
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
