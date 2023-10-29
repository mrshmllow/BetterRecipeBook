package marsh.town.brb.smithingtable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.armortrim.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

import java.util.List;
import java.util.Optional;

public class SmithableResult {
    public Ingredient template;
    public Ingredient base;
    public Ingredient addition;
    public ItemStack result;

    public SmithableResult(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static SmithableResult of(SmithingTransformRecipe recipe) {
        return new SmithableResult(recipe.template, recipe.base, recipe.addition, recipe.getResultItem(null));
    }

    public static SmithableResult of(SmithingTrimRecipe recipe) {
        ItemStack result = getTrimmedItem(recipe, Minecraft.getInstance().level.registryAccess());
        return new SmithableResult(recipe.template, recipe.base, recipe.addition, result);
    }

    private static ItemStack getTrimmedItem(SmithingTrimRecipe recipe, RegistryAccess registryAccess) {
        Optional<Holder.Reference<TrimMaterial>> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolder(TrimMaterials.REDSTONE);
        Optional<Holder.Reference<TrimPattern>> trim = TrimPatterns.getFromTemplate(registryAccess, recipe.template.getItems()[0]);

        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);

        if (material.isPresent() && trim.isPresent()) {
            ArmorTrim armorTri = new ArmorTrim(material.get(), trim.get());
            ArmorTrim.setTrim(registryAccess, itemStack, armorTri);
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
            if (base.test(slot.getItem())) return true;
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
