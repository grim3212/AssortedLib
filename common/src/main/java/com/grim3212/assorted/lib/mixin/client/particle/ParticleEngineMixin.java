package com.grim3212.assorted.lib.mixin.client.particle;

import com.grim3212.assorted.lib.core.block.effects.IBlockEffectSupplier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
    @Shadow
    protected ClientLevel level;

    @Inject(method = "crack", at = @At("HEAD"), cancellable = true)
    private void assortedlib_customHitEffects(BlockPos pos, Direction dir, CallbackInfo ci) {
        BlockState blockState = level.getBlockState(pos);
        if (blockState.getBlock() instanceof IBlockEffectSupplier extraProperties) {
            if (extraProperties.getClientEffects().get().addHitEffects(blockState, level, pos, dir, (ParticleEngine) (Object) this)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    private void assortedlib_customDestroyEffects(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (blockState.getBlock() instanceof IBlockEffectSupplier extraProperties) {
            if (extraProperties.getClientEffects().get().addDestroyEffects(blockState, level, blockPos, (ParticleEngine) (Object) this)) {
                ci.cancel();
            }
        }
    }
}
