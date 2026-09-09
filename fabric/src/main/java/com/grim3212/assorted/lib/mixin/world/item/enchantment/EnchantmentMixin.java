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

/**
 * Lets an item veto - or force - an enchantment being applicable to it.
 * <p>
 * {@link Enchantment} is a final record built from json in 26.2, so it can no longer be subclassed
 * and nothing can be an instance of {@link LibEnchantment} on its own. The interface is therefore
 * implemented onto {@code Enchantment} here, which is what makes the {@code instanceof} in the rest
 * of the library resolve at all; its default implementation returns
 * {@linkplain Optional#empty() empty}, so vanilla enchantments behave exactly as before unless
 * another mixin overrides it.
 */
@Mixin(Enchantment.class)
public abstract class EnchantmentMixin implements LibEnchantment {

    @Inject(
            method = "canEnchant",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void assortedlib_canEnchant(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        final Enchantment enchantment = ((Enchantment) (Object) this);

        Optional<Boolean> result = this.assortedlib_canApplyAtEnchantingTable(itemStack);
        if (result.isPresent()) {
            cir.setReturnValue(result.get());
            return;
        }

        if (itemStack.getItem() instanceof IItemEnchantmentCondition extension) {
            result = extension.assortedlib_canApplyAtEnchantingTable(itemStack, enchantment);
            if (result.isPresent()) {
                cir.setReturnValue(result.get());
            }
        }
    }
}
