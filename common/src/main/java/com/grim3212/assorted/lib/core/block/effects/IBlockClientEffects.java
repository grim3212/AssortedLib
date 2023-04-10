package com.grim3212.assorted.lib.core.block.effects;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockClientEffects {

    boolean addHitEffects(BlockState state, Level level, BlockPos pos, Direction dir, ParticleEngine manager);

    boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager);

}
