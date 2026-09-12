package com.grim3212.assorted.lib.mixin.world.level.chunk;

import com.grim3212.assorted.lib.core.block.IBlockLightEmission;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Decides whether a block change altered the light properties at that position, rather than only in
 * the abstract.
 * <p>
 * {@link LightEngine#hasDifferentLightProperties(BlockState, BlockState)} compares two states and
 * has no position, so an {@link IBlockLightEmission} block - whose emission comes from its block
 * entity - cannot be compared honestly there. NeoForge patches vanilla to pass the level and
 * position ({@code hasDifferentLightProperties(BlockGetter, BlockPos, BlockState, BlockState)});
 * Fabric has no such patch, so the call site is redirected here instead. {@code setBlockState} takes
 * the position as a parameter, which is what makes this possible at all.
 * <p>
 * Answering per position rather than conservatively is what keeps a light re-check from being queued
 * for every change involving one of these blocks. The other caller of the vanilla method,
 * {@code ProtoChunk}, is left alone: worldgen has no level and no block entities to ask.
 */
@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {

    @Shadow
    public abstract Level getLevel();

    @Redirect(
            method = "setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/LightEngine;hasDifferentLightProperties(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean assortedlib_lightPropertiesDifferHere(BlockState oldState, BlockState newState, BlockPos pos, BlockState placed, int flags) {
        if (newState == oldState) {
            return false;
        }

        // Everything but the emission is a property of the state alone, so it is compared exactly as
        // vanilla does; only the emission gains the position.
        if (newState.getLightDampening() != oldState.getLightDampening()
                || newState.useShapeForLightOcclusion()
                || oldState.useShapeForLightOcclusion()) {
            return true;
        }

        final Level level = getLevel();
        return assortedlib_lightEmission(oldState, level, pos) != assortedlib_lightEmission(newState, level, pos);
    }

    private static int assortedlib_lightEmission(final BlockState blockState, final BlockGetter blockGetter, final BlockPos pos) {
        if (blockState.getBlock() instanceof IBlockLightEmission emission) {
            return emission.getLightEmission(blockState, blockGetter, pos);
        }

        return blockState.getLightEmission();
    }
}
