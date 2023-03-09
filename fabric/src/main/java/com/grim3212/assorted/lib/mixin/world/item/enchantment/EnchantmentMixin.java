package com.grim3212.assorted.lib.mixin.world.item.enchantment;

import com.grim3212.assorted.lib.core.item.IItemEnchantmentCondition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(
            method = "canEnchant",
            at = @At("HEAD"),
            cancellable = true
    )
    private void assortedlib_canEnchant(final ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof IItemEnchantmentCondition extraProperties) {
            cir.setReturnValue(extraProperties.canApplyAtEnchantingTable(stack, (Enchantment) (Object) this));
        }
    }
}
