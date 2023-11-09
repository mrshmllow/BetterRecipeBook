package marsh.town.brb.recipe;

import marsh.town.brb.generic.GenericRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;

import java.util.List;

public interface BRBSmithingRecipe extends SmithingRecipe, GenericRecipe {
    ItemStack getResult(RegistryAccess registryAccess);

    ItemStack getResult(ResourceKey<TrimMaterial> trimMaterialResourceKey, RegistryAccess registryAccess);

    Ingredient getTemplate();

    ItemStack getBase();

    Ingredient getAddition();

    default boolean hasMaterials(NonNullList<Slot> slots, RegistryAccess registryAccess) {
        return hasTemplate(slots) && hasBase(slots, registryAccess) && hasAddition(slots);
    }

    default boolean hasTemplate(List<Slot> slots) {
        for (Slot slot : slots) {
            if (this.getTemplate().test(slot.getItem())) return true;
        }
        return false;
    }

    default boolean hasBase(List<Slot> slots, RegistryAccess registryAccess) {
        for (Slot slot : slots) {
            if (ArmorTrim.getTrim(registryAccess, slot.getItem(), true).isEmpty() && getBase().getItem().equals(slot.getItem().getItem()))
                return true;
        }
        return false;
    }

    default boolean hasAddition(List<Slot> slots) {
        for (Slot slot : slots) {
            if (getAddition().test(slot.getItem())) return true;
        }
        return false;
    }

    default String getTemplateType() {
        return getTemplate().getItems()[0].getTooltipLines(Minecraft.getInstance().player, TooltipFlag.NORMAL).get(1).getString();
    }

    default ResourceLocation id() {
        return BuiltInRegistries.ITEM.getKey(getTemplate().getItems()[0].getItem());
    }
}
