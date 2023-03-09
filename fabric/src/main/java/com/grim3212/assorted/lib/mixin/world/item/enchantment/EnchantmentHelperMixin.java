package com.grim3212.assorted.lib.mixin.world.item.enchantment;

import com.grim3212.assorted.lib.core.item.IItemEnchantmentCondition;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Unique
    private static Enchantment currentEnchantment;

    @Redirect(method = "getAvailableEnchantmentResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentCategory;canEnchant(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean isAcceptableItem(EnchantmentCategory enchantmentTarget, Item item) {
        ItemStack stack = new ItemStack(item);

        if (item instanceof IItemEnchantmentCondition extraProperties) {
            return extraProperties.canApplyAtEnchantingTable(stack, currentEnchantment);
        }

        return enchantmentTarget.canEnchant(item);
    }
}
