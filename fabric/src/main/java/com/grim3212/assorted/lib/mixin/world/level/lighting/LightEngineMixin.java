package com.grim3212.assorted.lib.mixin.world.level.lighting;

import com.grim3212.assorted.lib.core.block.IBlockLightEmission;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightEngine.class)
public class LightEngineMixin {

    // TODO(26.2): hasDifferentLightProperties lost its BlockGetter and BlockPos parameters - it is
    //  now hasDifferentLightProperties(BlockState, BlockState), called from LevelChunk#setBlockState
    //  and ProtoChunk#setBlockState with nothing but the two states. IBlockLightEmission asks for a
    //  level and a position to compute an emission, so the real value cannot be worked out here
    //  anymore. The check is answered conservatively instead: whenever either side of the change is
    //  an IBlockLightEmission block the light properties are reported as different, which queues a
    //  LightEngine#checkBlock for the position. The actual position dependent emission is then
    //  supplied by BlockLightEngineMixin#assortedlib_onGetEmission, which does have a position. The
    //  cost is a light re-check for some changes that would not have needed one; the previous
    //  behaviour of skipping the re-check when the emission happened to be unchanged is lost.
    @Inject(method = "hasDifferentLightProperties", at = @At("HEAD"), cancellable = true)
    private static void assortedlib_onHasDifferentLightProperties(BlockState oldState, BlockState newState, CallbackInfoReturnable<Boolean> cir) {
        if (oldState == newState) {
            return;
        }

        if (oldState.getBlock() instanceof IBlockLightEmission || newState.getBlock() instanceof IBlockLightEmission) {
            cir.setReturnValue(true);
        }
    }
}
