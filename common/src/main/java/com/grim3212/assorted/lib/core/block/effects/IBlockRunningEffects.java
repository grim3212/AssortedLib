package com.grim3212.assorted.lib.core.block.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockRunningEffects {
    boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity);
}
