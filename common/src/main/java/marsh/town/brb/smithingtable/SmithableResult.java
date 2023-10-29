package marsh.town.brb.smithingtable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

import java.util.List;

import static marsh.town.brb.brewingstand.PlatformPotionUtil.getIngredient;

public class SmithableResult {
    public ItemStack template;
    public ItemStack base;
    public ItemStack addition;
    public ItemStack result;

    public SmithableResult(ItemStack template, ItemStack base, ItemStack addition, ItemStack result) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static SmithableResult of(SmithingTransformRecipe recipe) {
        return new SmithableResult(recipe.template.getItems()[0], recipe.base.getItems()[0], recipe.addition.getItems()[0], recipe.getResultItem(null));
    }

    public static SmithableResult of(SmithingTrimRecipe recipe) {
        ItemStack result = recipe.getResultItem(Minecraft.getInstance().getConnection().registryAccess());
        return new SmithableResult(recipe.template.getItems()[0], recipe.base.getItems()[0], recipe.addition.getItems()[0], result);
    }

    public boolean hasTemplate(List<Slot> slots) {
        for (Slot slot : slots) {
            if (template.getItem().equals(slot.getItem().getItem())) return true;
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
            if (addition.getItem().equals(slot.getItem().getItem())) return true;
        }
        return false;
    }

    public boolean hasMaterials(SmithingRecipeBookGroup group, NonNullList<Slot> slots) {
        return hasTemplate(slots) && hasBase(slots) && hasAddition(slots);
    }
}
