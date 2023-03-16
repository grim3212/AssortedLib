package com.grim3212.assorted.lib.mixin.item.enchantment;

import com.grim3212.assorted.lib.core.item.IItemEnchantmentCondition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(IItemEnchantmentCondition.class)
public interface IItemEnchantmentConditionMixin extends IForgeItem {
    @Shadow
    Optional<Boolean> assortedlib_canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment);

    @Override
    default boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return this.assortedlib_canApplyAtEnchantingTable(stack, enchantment).orElseGet(() -> IForgeItem.super.canApplyAtEnchantingTable(stack, enchantment));
    }
}
