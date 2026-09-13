package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block whose light dampening depends on where it is, not only on its state - one that keeps what
 * it stands in for in its block entity, such as a colorizer. This is what
 * {@code ILevelPropertyAccessor} asks, beside {@link IBlockLightEmission}; it does not reach the
 * light engines. Vanilla bakes {@code lightDampening} into the block state and neither loader offers
 * a position-aware hook, so a block whose dampening must really darken the world carries it in a
 * block state property instead, as the colorizer's full cubes do.
 */
public interface IBlockLightDampening {

    /**
     * How much light this block takes out of what passes through it, 0-15, as vanilla's
     * {@code BlockState#getLightDampening} would answer for the block it stands in for.
     */
    int getLightDampening(BlockState state, BlockGetter blockGetter, BlockPos pos);

    /**
     * Whether skylight passes straight down through this block, as vanilla's
     * {@code BlockState#propagatesSkylightDown} would answer for the block it stands in for.
     */
    default boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return state.propagatesSkylightDown();
    }
}
