package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockLightEmission {
    int getLightEmission(BlockState state, BlockGetter blockGetter, BlockPos pos);
}
