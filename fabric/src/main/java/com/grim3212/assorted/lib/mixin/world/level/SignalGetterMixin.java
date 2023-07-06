package com.grim3212.assorted.lib.mixin.world.level;

import com.grim3212.assorted.lib.core.block.IBlockExtraProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin {

    private SignalGetter getInternalMixinTarget() {
        return (SignalGetter) this;
    }

    @Inject(
            method = "getSignal",
            at = @At("HEAD"),
            cancellable = true
    )
    public default void assortedlib_checkForWorldlySignalBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        final BlockState blockState = getInternalMixinTarget().getBlockState(blockPos);
        if (blockState.getBlock() instanceof IBlockExtraProperties extraProperties) {
            final boolean shouldCheck = extraProperties.shouldCheckWeakPower(blockState, getInternalMixinTarget(), blockPos, direction);

            final int signal = blockState.getSignal(getInternalMixinTarget(), blockPos, direction);
            cir.setReturnValue(shouldCheck ? Math.max(signal, getInternalMixinTarget().getDirectSignalTo(blockPos)) : signal);
        }
    }
}
