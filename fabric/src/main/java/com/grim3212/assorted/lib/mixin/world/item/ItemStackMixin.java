package com.grim3212.assorted.lib.mixin.world.item;

import com.grim3212.assorted.lib.core.item.IItemExtraProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean isDamageableItem();

    @Inject(
            method = "getDamageValue",
            at = @At("HEAD"),
            cancellable = true
    )
    private void assortedlib_getDamageValue(CallbackInfoReturnable<Integer> cir) {
        if (this.getItem() instanceof IItemExtraProperties extraProperties) {
            cir.setReturnValue(extraProperties.getDamage((ItemStack) (Object) this));
        }
    }

    @Inject(
            method = "getMaxDamage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void assortedlib_getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (this.getItem() instanceof IItemExtraProperties extraProperties) {
            cir.setReturnValue(extraProperties.getMaxDamage((ItemStack) (Object) this));
        }
    }

    @Inject(
            method = "isDamaged",
            at = @At("HEAD"),
            cancellable = true
    )
    private void assortedlib_isDamaged(CallbackInfoReturnable<Boolean> cir) {
        if (this.getItem() instanceof IItemExtraProperties extraProperties) {
            cir.setReturnValue(this.isDamageableItem() && extraProperties.isDamaged((ItemStack) (Object) this));
        }
    }

    @Inject(
            method = "setDamageValue",
            at = @At("HEAD"),
            cancellable = true
    )
    private void assortedlib_setDamageValue(final int damage, CallbackInfo cir) {
        if (this.getItem() instanceof IItemExtraProperties extraProperties) {
            extraProperties.setDamage((ItemStack) (Object) this, damage);
            cir.cancel();
        }
    }
}
