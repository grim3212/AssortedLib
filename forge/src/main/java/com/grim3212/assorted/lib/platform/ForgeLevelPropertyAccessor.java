package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.ILevelPropertyAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.Nullable;

public class ForgeLevelPropertyAccessor implements ILevelPropertyAccessor {
    @Override
    public boolean shouldCheckWeakPower(LevelReader levelReader, BlockPos blockPos, Direction direction) {
        return levelReader.getBlockState(blockPos).shouldCheckWeakPower(levelReader, blockPos, direction);
    }

    @Override
    public float getFriction(LevelReader levelReader, BlockPos blockPos, @Nullable Entity entity) {
        return levelReader.getBlockState(blockPos).getFriction(levelReader, blockPos, entity);
    }

    @Override
    public int getLightEmission(LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBlockState(blockPos).getLightEmission(levelReader, blockPos);
    }

    @Override
    public boolean canHarvestBlock(BlockGetter blockGetter, BlockPos pos, Player player) {
        return blockGetter.getBlockState(pos).canHarvestBlock(blockGetter, pos, player);
    }

    @Override
    public SoundType getSoundType(LevelReader levelReader, BlockPos pos, Entity entity) {
        return levelReader.getBlockState(pos).getSoundType(levelReader, pos, entity);
    }

    @Override
    public float getExplosionResistance(BlockGetter blockGetter, BlockPos position, Explosion explosion) {
        return blockGetter.getBlockState(position).getExplosionResistance(blockGetter, position, explosion);
    }
}
