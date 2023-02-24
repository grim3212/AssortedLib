package com.grim3212.assorted.lib.mixin.world.item;

import com.grim3212.assorted.lib.core.item.IItemExtraProperties;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Shadow
    public int maxDamage;

    @Inject(
            method = "getBarWidth",
            at = @At("RETURN"),
            cancellable = true
    )
    private void assortedlib_getBarWidth(final ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (this instanceof IItemExtraProperties extraProperties) {
            int retValue = cir.getReturnValue();
            int newBarWidth = Math.round(13.0F - (float) stack.getDamageValue() * 13.0F / (float) extraProperties.getMaxDamage(stack));
            // In case this has already been overwritten then we need to account for it
            if (retValue != newBarWidth) {
                cir.setReturnValue(retValue);
            } else {
                cir.setReturnValue(newBarWidth);
            }
        }
    }

    @Inject(
            method = "getBarColor",
            at = @At("RETURN"),
            cancellable = true
    )
    private void assortedlib_getBarColor(final ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (this instanceof IItemExtraProperties extraProperties) {
            int retValue = cir.getReturnValue();
            float stackMaxDamage = extraProperties.getMaxDamage(stack);
            float f = Math.max(0.0F, (stackMaxDamage - (float) extraProperties.getDamage(stack)) / stackMaxDamage);
            int newBarColor = Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
            // In case this has already been overwritten then we need to account for it
            if (retValue != newBarColor) {
                cir.setReturnValue(retValue);
            } else {
                cir.setReturnValue(newBarColor);
            }
        }
    }
}
