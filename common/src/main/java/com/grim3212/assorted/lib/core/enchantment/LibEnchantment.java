package com.grim3212.assorted.lib.core.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Optional;

public class LibEnchantment extends Enchantment {
    protected LibEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
        super(rarity, category, slots);
    }

    public Optional<Boolean> assortedlib_canApplyAtEnchantingTable(ItemStack stack) {
        return Optional.empty();
    }
}
