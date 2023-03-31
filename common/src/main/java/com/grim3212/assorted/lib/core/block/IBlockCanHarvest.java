package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockCanHarvest {
    boolean canHarvestBlock(BlockState state, BlockGetter blockGetter, BlockPos pos, Player player);
}
