package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;


public interface IBlockMapColor {

    MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor);
}
