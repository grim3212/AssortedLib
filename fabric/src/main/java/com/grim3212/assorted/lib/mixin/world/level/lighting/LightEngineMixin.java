package com.grim3212.assorted.lib.mixin.world.level.lighting;

import com.grim3212.assorted.lib.core.block.IBlockLightEmission;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightEngine.class)
public class LightEngineMixin {

    @Inject(method = "hasDifferentLightProperties", at = @At("HEAD"), cancellable = true)
    private static void assortedlib_onHasDifferentLightProperties(BlockGetter blockGetter, BlockPos blockPos, BlockState oldState, BlockState newState, CallbackInfoReturnable<Boolean> cir) {
        if (oldState == newState) {
            return;
        }

        if (!(oldState.getBlock() instanceof IBlockLightEmission)) {
            if (!(newState.getBlock() instanceof IBlockLightEmission)) {
                return;
            }
        }

        final int oldLight = oldState.getBlock() instanceof IBlockLightEmission oldLightEmission ? oldLightEmission.getLightEmission(oldState, blockGetter, blockPos) : oldState.getLightEmission();

        if (!(newState.getBlock() instanceof final IBlockLightEmission newWithWorldlyProperties)) {
            cir.setReturnValue(newState.getLightBlock(blockGetter, blockPos) != oldState.getLightBlock(blockGetter, blockPos) || newState.getLightEmission() != oldLight || newState.useShapeForLightOcclusion() || oldState.useShapeForLightOcclusion());
            return;
        }

        cir.setReturnValue(newState.getLightBlock(blockGetter, blockPos) != oldState.getLightBlock(blockGetter, blockPos) || newWithWorldlyProperties.getLightEmission(newState, blockGetter, blockPos) != oldLight || newState.useShapeForLightOcclusion() || oldState.useShapeForLightOcclusion());
    }
}
