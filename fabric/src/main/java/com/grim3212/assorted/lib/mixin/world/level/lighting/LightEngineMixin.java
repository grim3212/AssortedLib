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

    // TODO(26.2): hasDifferentLightProperties only gets the two states, no level or position, so an
    //  IBlockLightEmission block's real emission cannot be computed here. Any change involving one
    //  is reported as different, queuing a light check that
    //  BlockLightEngineMixin#assortedlib_onGetEmission answers with the position. Cost: some
    //  changes with an unchanged emission are re-checked.
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
