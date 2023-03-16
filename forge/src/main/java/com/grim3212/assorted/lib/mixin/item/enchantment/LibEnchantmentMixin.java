package com.grim3212.assorted.lib.mixin.item.enchantment;

import com.grim3212.assorted.lib.core.enchantment.LibEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(LibEnchantment.class)
public abstract class LibEnchantmentMixin extends Enchantment {

    protected LibEnchantmentMixin(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
        super(rarity, category, slots);
    }

    @Shadow
    abstract Optional<Boolean> assortedlib_canApplyAtEnchantingTable(ItemStack stack);

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return this.assortedlib_canApplyAtEnchantingTable(stack).orElseGet(() -> super.canApplyAtEnchantingTable(stack));
    }
}
