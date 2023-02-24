package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

/**
 * From https://github.com/marchermans/Multi-Platform-Template
 * License MIT: https://github.com/marchermans/Multi-Platform-Template/blob/master/LICENSE.md
 */
public interface IBlockExtraProperties {

    boolean shouldCheckWeakPower(BlockState state, LevelReader levelReader, BlockPos pos, Direction side);

    float getFriction(BlockState state, LevelReader levelReader, BlockPos pos, @Nullable Entity entity);

    int getLightEmission(BlockState state, BlockGetter blockGetter, BlockPos pos);

    boolean canHarvestBlock(BlockState state, BlockGetter blockGetter, BlockPos pos, Player player);

    ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter blockGetter, BlockPos pos, Player player);

    SoundType getSoundType(BlockState state, LevelReader levelReader, BlockPos pos, @Nullable Entity entity);

    float getExplosionResistance(BlockState state, BlockGetter blockGetter, BlockPos position, Explosion explosion);
}
