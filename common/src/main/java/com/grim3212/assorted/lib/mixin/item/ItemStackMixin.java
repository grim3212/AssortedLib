package com.grim3212.assorted.lib.mixin.item;

import com.grim3212.assorted.lib.core.item.IItemCorrectDrops;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "isCorrectToolForDrops", at = @At("HEAD"), cancellable = true)
    public void assortedlib_isCorrectToolForDrops(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.getItem() instanceof IItemCorrectDrops extraProperties) {
            cir.setReturnValue(extraProperties.isCorrectToolForDrops(stack, state));
        }
    }
}
