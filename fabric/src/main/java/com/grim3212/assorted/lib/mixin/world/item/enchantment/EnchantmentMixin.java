package com.grim3212.assorted.lib.mixin.world.item.enchantment;

import com.grim3212.assorted.lib.core.enchantment.LibEnchantment;
import com.grim3212.assorted.lib.core.item.IItemEnchantmentCondition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(
            method = "canEnchant",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void assortedlib_canEnchant(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = ((Enchantment) (Object) this);
        if (enchantment instanceof LibEnchantment libEnchantment) {
            Optional<Boolean> result = libEnchantment.assortedlib_canApplyAtEnchantingTable(itemStack);
            if (result.isPresent()) {
                cir.setReturnValue(result.get());
                return;
            }
        }
        if (itemStack.getItem() instanceof IItemEnchantmentCondition extension) {
            Optional<Boolean> result = extension.assortedlib_canApplyAtEnchantingTable(itemStack, enchantment);
            if (result.isPresent()) {
                cir.setReturnValue(result.get());
            }
        }
    }
}
