package com.grim3212.assorted.lib.core.block.effects;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ServerEffectUtils {
    private static final Random RANDOM = new Random();

    public static boolean addLandingEffects(BlockState state, ServerLevel level, LivingEntity entity, int numberOfParticles) {
        if (state != null && !state.isAir()) {
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), entity.getX(), entity.getY(), entity.getZ(), numberOfParticles, 0.0, 0.0, 0.0, 0.15000000596046448);
            return true;
        }

        return false;
    }

    public static boolean addRunningEffects(BlockState state, Level level, Entity entity) {
        if (state != null && !state.isAir()) {
            Vec3 vec3 = entity.getDeltaMovement();
            EntityDimensions dimensions = entity.getType().getDimensions();
            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), entity.getX() + (RANDOM.nextDouble() - 0.5) * (double) dimensions.width, entity.getY() + 0.1, entity.getZ() + (RANDOM.nextDouble() - 0.5) * (double) dimensions.width, vec3.x * -4.0, 1.5, vec3.z * -4.0);
            return true;
        }

        return false;
    }
}
