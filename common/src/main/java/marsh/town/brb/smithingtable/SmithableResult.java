package marsh.town.brb.smithingtable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
    private boolean isTransform;

    public SmithableResult(Ingredient template, ItemStack base, Ingredient addition, ItemStack result, boolean isTransform) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
        this.isTransform = isTransform;
    }

    public static SmithableResult of(SmithingTransformRecipe recipe) {
        return new SmithableResult(recipe.template, recipe.base.getItems()[0], recipe.addition, recipe.getResultItem(null), true);
    }

    public static List<SmithableResult> of(SmithingTrimRecipe recipe) {
        List<SmithableResult> results = new ArrayList<>();

        for (ItemStack base: recipe.base.getItems()) {
            ItemStack result = getTrimmedItem(recipe, base, TrimMaterials.REDSTONE, Minecraft.getInstance().getConnection().registryAccess());

            results.add(new SmithableResult(recipe.template, base, recipe.addition, result, false));
        }

        return results;
    }

    private static ItemStack getTrimmedItem(SmithingTrimRecipe recipe, ItemStack base, ResourceKey<TrimMaterial> trim_mat, RegistryAccess registryAccess) {
        Optional<Holder.Reference<TrimMaterial>> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolder(trim_mat);
        Optional<Holder.Reference<TrimPattern>> trim = TrimPatterns.getFromTemplate(registryAccess, recipe.template.getItems()[0]);

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

    public ItemStack getTrimmedResult(Holder<TrimMaterial> trim_mat, RegistryAccess registryAccess) {
        if (this.isTransform) {
            return this.result;
        }

        Optional<Holder.Reference<TrimPattern>> trim = TrimPatterns.getFromTemplate(registryAccess, this.template.getItems()[0]);

        ItemStack itemStack = this.base.copy();

        if (trim.isPresent()) {
            ArmorTrim armorTri = new ArmorTrim(trim_mat, trim.get());
            ArmorTrim.setTrim(registryAccess, this.base, armorTri);
        }

        return itemStack;
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

    public boolean hasMaterials(SmithingRecipeBookGroup group, NonNullList<Slot> slots) {
        return hasTemplate(slots) && hasBase(slots) && hasAddition(slots);
    }

    public String getTemplateType() {
        return template.getItems()[0].getTooltipLines(Minecraft.getInstance().player, TooltipFlag.NORMAL).get(1).getString();
    }
}
