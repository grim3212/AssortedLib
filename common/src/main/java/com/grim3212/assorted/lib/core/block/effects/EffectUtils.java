package com.grim3212.assorted.lib.core.block.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class EffectUtils {
    private static final Random RANDOM = new Random();

    public static void addBlockDestroyEffects(final LevelReader levelReader, final BlockPos pos, final BlockState primaryState, final ParticleEngine manager, final Level renderingWorld) {
        if (!primaryState.isAir()) {
            final VoxelShape voxelshape = levelReader.getBlockState(pos).getShape(levelReader, pos);
            voxelshape.forAllBoxes((p_228348_3_, p_228348_5_, p_228348_7_, p_228348_9_, p_228348_11_, p_228348_13_) -> {
                final double d1 = Math.min(1.0D, p_228348_9_ - p_228348_3_);
                final double d2 = Math.min(1.0D, p_228348_11_ - p_228348_5_);
                final double d3 = Math.min(1.0D, p_228348_13_ - p_228348_7_);
                final int i = Math.max(2, Mth.ceil(d1 / 0.25D));
                final int j = Math.max(2, Mth.ceil(d2 / 0.25D));
                final int k = Math.max(2, Mth.ceil(d3 / 0.25D));

                for (int l = 0; l < i; ++l) {
                    for (int i1 = 0; i1 < j; ++i1) {
                        for (int j1 = 0; j1 < k; ++j1) {
                            final double d4 = ((double) l + 0.5D) / (double) i;
                            final double d5 = ((double) i1 + 0.5D) / (double) j;
                            final double d6 = ((double) j1 + 0.5D) / (double) k;
                            final double d7 = d4 * d1 + p_228348_3_;
                            final double d8 = d5 * d2 + p_228348_5_;
                            final double d9 = d6 * d3 + p_228348_7_;
                            manager.add((new TerrainParticle((ClientLevel) renderingWorld,
                                    (double) pos.getX() + d7,
                                    (double) pos.getY() + d8,
                                    (double) pos.getZ() + d9,
                                    d4 - 0.5D,
                                    d5 - 0.5D,
                                    d6 - 0.5D,
                                    primaryState, pos)));
                        }
                    }
                }
            });
        }
    }

    public static boolean addHitEffects(final Level world, final BlockPos pos, final Direction direction, final BlockState primaryState, final ParticleEngine manager) {
        if (Minecraft.getInstance().player == null)
            return false;

        final double boxOffset = 0.1;

        final AABB bb = world.getBlockState(pos).getShape(world, pos).bounds();

        double x = pos.getX() + RANDOM.nextDouble() * (bb.maxX - bb.minX - boxOffset * 2d) + boxOffset + bb.minX;
        double y = pos.getY() + RANDOM.nextDouble() * (bb.maxY - bb.minY - boxOffset * 2d) + boxOffset + bb.minY;
        double z = pos.getZ() + RANDOM.nextDouble() * (bb.maxZ - bb.minZ - boxOffset * 2d) + boxOffset + bb.minZ;

        switch (direction) {
            case DOWN -> y = pos.getY() + bb.minY - boxOffset;
            case UP -> y = pos.getY() + bb.maxY + boxOffset;
            case NORTH -> z = pos.getZ() + bb.minZ - boxOffset;
            case SOUTH -> z = pos.getZ() + bb.maxZ + boxOffset;
            case WEST -> x = pos.getX() + bb.minX - boxOffset;
            case EAST -> x = pos.getX() + bb.maxX + boxOffset;
            default -> {
            }
        }

        manager.add((new TerrainParticle((ClientLevel) world, x, y, z, 0.0D, 0.0D, 0.0D, primaryState, pos))
                .setPower(0.2F)
                .scale(0.6F));

        return true;
    }
}
