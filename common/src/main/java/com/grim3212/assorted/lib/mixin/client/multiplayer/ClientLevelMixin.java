package com.grim3212.assorted.lib.mixin.client.multiplayer;

import com.grim3212.assorted.lib.core.block.effects.IBlockEffectSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Lets an {@link IBlockEffectSupplier} block draw its own hit and break particles.
 * <p>
 * The 1.20.1 hooks were {@code ParticleEngine#crack(BlockPos, Direction)} and
 * {@code ParticleEngine#destroy(BlockPos, BlockState)}. 26.2 moved both out of
 * {@link net.minecraft.client.particle.ParticleEngine} - which now only owns the particle lists -
 * onto {@link ClientLevel} as {@code addBreakingBlockEffect} and {@code addDestroyBlockEffect},
 * so the injections follow them there.
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "addBreakingBlockEffect(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)V", at = @At("HEAD"), cancellable = true)
    private void assortedlib_customHitEffects(BlockPos pos, Direction dir, CallbackInfo ci) {
        final ClientLevel level = (ClientLevel) (Object) this;
        BlockState blockState = level.getBlockState(pos);
        if (blockState.getBlock() instanceof IBlockEffectSupplier extraProperties) {
            if (extraProperties.getClientEffects().get().addHitEffects(blockState, level, pos, dir, Minecraft.getInstance().particleEngine)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "addDestroyBlockEffect(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("HEAD"), cancellable = true)
    private void assortedlib_customDestroyEffects(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (blockState.getBlock() instanceof IBlockEffectSupplier extraProperties) {
            if (extraProperties.getClientEffects().get().addDestroyEffects(blockState, (ClientLevel) (Object) this, blockPos, Minecraft.getInstance().particleEngine)) {
                ci.cancel();
            }
        }
    }
}
