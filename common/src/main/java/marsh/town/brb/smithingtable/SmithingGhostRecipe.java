package marsh.town.brb.smithingtable;

import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.generic.GenericGhostRecipe;
import marsh.town.brb.mixins.accessors.HolderReferenceAccessor;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import marsh.town.brb.recipe.smithing.BRBSmithingTransformRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.*;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SmithingGhostRecipe extends GenericGhostRecipe<BRBSmithingRecipe> {
    public SmithingGhostRecipe(@Nullable Consumer<ItemStack> onGhostUpdate, RegistryAccess registryAccess) {
        super(onGhostUpdate, registryAccess);
    }

    @Override
    public ItemStack getCurrentResult(BRBBookCategories.Category category) {
        if (this.recipe == null) {
            return ItemStack.EMPTY;
        }

        if (this.recipe instanceof BRBSmithingTransformRecipe) {
            return this.recipe.getResult(registryAccess, category);
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
            itemStack.set(DataComponents.TRIM, armorTri);
        }

        return itemStack;
    }
}
