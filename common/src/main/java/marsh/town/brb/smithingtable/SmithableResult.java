package marsh.town.brb.smithingtable;

import marsh.town.brb.mixins.accessors.SmithingTransformRecipeAccessor;
import marsh.town.brb.mixins.accessors.SmithingTrimRecipeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.armortrim.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SmithableResult {
    public Ingredient template;
    public ItemStack base;
    public Ingredient addition;
    public ItemStack result;

    public boolean isTransform;

    public SmithableResult(Ingredient template, ItemStack base, Ingredient addition, ItemStack result, boolean isTransform) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
        this.isTransform = isTransform;
    }

    public static SmithableResult of(SmithingTransformRecipe recipe) {
        SmithingTransformRecipeAccessor r = (SmithingTransformRecipeAccessor) recipe;

        return new SmithableResult(r.getTemplate(), r.getBase().getItems()[0], r.getAddtion(), recipe.getResultItem(null), true);
    }

    public static List<SmithableResult> of(SmithingTrimRecipe recipe) {
        List<SmithableResult> results = new ArrayList<>();
        SmithingTrimRecipeAccessor r = (SmithingTrimRecipeAccessor) recipe;

        for (ItemStack base : r.getBase().getItems()) {
            ItemStack result = getTrimmedItem(recipe, base, TrimMaterials.REDSTONE, Minecraft.getInstance().getConnection().registryAccess());

            results.add(new SmithableResult(r.getTemplate(), base, r.getAddtion(), result, false));
        }

        return results;
    }

    public static ItemStack getTrimmedItem(SmithingTrimRecipe recipe, ItemStack base, ResourceKey<TrimMaterial> trim_mat, RegistryAccess registryAccess) {
        Optional<Holder.Reference<TrimMaterial>> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolder(trim_mat);
        Optional<Holder.Reference<TrimPattern>> trim = TrimPatterns.getFromTemplate(registryAccess, ((SmithingTrimRecipeAccessor) recipe).getTemplate().getItems()[0]);

        ItemStack itemStack = base.copy();

        if (material.isPresent() && trim.isPresent()) {
            ArmorTrim armorTri = new ArmorTrim(material.get(), trim.get());
            ArmorTrim.setTrim(registryAccess, itemStack, armorTri);
        }

        return itemStack;
    }

    public List<Holder.Reference<TrimMaterial>> getPossibleTrims(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).holders().toList();
    }

    public boolean hasTemplate(List<Slot> slots) {
        for (Slot slot : slots) {
            if (template.test(slot.getItem())) return true;
        }
        return false;
    }

    public boolean hasBase(List<Slot> slots) {
        for (Slot slot : slots) {
            if (base.getItem().equals(slot.getItem().getItem())) return true;
        }
        return false;
    }

    public boolean hasAddition(List<Slot> slots) {
        for (Slot slot : slots) {
            if (addition.test(slot.getItem())) return true;
        }
        return false;
    }

    public boolean hasMaterials(NonNullList<Slot> slots) {
        return hasTemplate(slots) && hasBase(slots) && hasAddition(slots);
    }

    public String getTemplateType() {
        return template.getItems()[0].getTooltipLines(Minecraft.getInstance().player, TooltipFlag.NORMAL).get(1).getString();
    }
}
