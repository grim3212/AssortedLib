package com.grim3212.assorted.lib.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;

import javax.annotation.Nullable;

public interface ILevelPropertyAccessor {

    boolean shouldCheckWeakPower(LevelReader levelReader, BlockPos blockPos, Direction direction);

    float getFriction(LevelReader levelReader, BlockPos blockPos, @Nullable Entity entity);

    int getLightEmission(LevelReader levelReader, BlockPos blockPos);

    default int getLightBlock(BlockGetter blockGetter, BlockPos blockPos) {
        return blockGetter.getBlockState(blockPos).getLightBlock(
                blockGetter,
                blockPos
        );
    }

    default boolean propagatesSkylightDown(BlockGetter blockGetter, BlockPos blockPos) {
        return blockGetter.getBlockState(blockPos).propagatesSkylightDown(blockGetter, blockPos);
    }

    boolean canHarvestBlock(BlockGetter blockGetter, BlockPos pos, Player player);

    SoundType getSoundType(final LevelReader levelReader, BlockPos pos, Entity entity);

    float getExplosionResistance(BlockGetter blockGetter, BlockPos position, Explosion explosion);
}
