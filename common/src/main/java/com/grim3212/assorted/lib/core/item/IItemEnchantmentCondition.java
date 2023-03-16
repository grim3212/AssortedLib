package com.grim3212.assorted.lib.core.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public interface IItemEnchantmentCondition {

    default Optional<Boolean> assortedlib_canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return Optional.empty();
    }
}
