package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface IPlantSustainable {
    boolean canSustainPlant(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction direction, Block plant);
}
