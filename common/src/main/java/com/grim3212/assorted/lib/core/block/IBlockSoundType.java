package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface IBlockSoundType {
    SoundType getSoundType(BlockState state, LevelReader levelReader, BlockPos pos, @Nullable Entity entity);
}
