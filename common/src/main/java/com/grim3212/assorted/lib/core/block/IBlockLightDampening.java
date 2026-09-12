package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block whose light dampening depends on where it is, not only on its state - a block that keeps
 * what it should look and behave like in its block entity, such as a colorizer. This is what
 * {@code ILevelPropertyAccessor} asks, beside {@link IBlockLightEmission}.
 * <p>
 * It does not reach the light engines. Vanilla bakes {@code lightDampening} into the block state
 * ({@code BlockStateBase#initCache}) and neither loader offers a position-aware hook for it, so a
 * block whose dampening must actually darken the world has to carry it in a block state property,
 * as the colorizer's full cubes do; vanilla then relights, recomputes the sky column and tells the
 * clients on its own. Implementing this only keeps the library's answer honest for the shapes that
 * do not.
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
