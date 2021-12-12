package net.marshmallow.BetterRecipeBook.Mixins.Accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@Mixin(Inventory.class)
public interface PlayerInventoryAccessor {
    @Accessor("compartments")
    List<NonNullList<ItemStack>> getCompartments();
}
