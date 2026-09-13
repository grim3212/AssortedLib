package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * The {@code super} calls are vanilla accessors that NeoForge deprecates for level-aware versions
 * which only exist in its patched jar; common builds against vanilla, so nothing else is available.
 */
@SuppressWarnings("deprecation")
public class ExtraPropertyBlock extends Block implements IBlockExtraProperties, IBlockSoundType, IBlockCanHarvest, IBlockLightEmission {
    public ExtraPropertyBlock(Properties props) {
        super(props);
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, SignalGetter signalGetter, BlockPos pos, Direction side) {
        return state.isRedstoneConductor(signalGetter, pos);
    }

    @Override
    public float getFriction(BlockState state, LevelReader levelReader, BlockPos pos, @Nullable Entity entity) {
        return super.getFriction();
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return state.getLightEmission();
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter blockGetter, BlockPos pos, Player player) {
        return player.hasCorrectToolForDrops(state);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader levelReader, BlockPos pos, @Nullable Entity entity) {
        return super.getSoundType(state);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter blockGetter, BlockPos position, Explosion explosion) {
        return super.getExplosionResistance();
    }
}
