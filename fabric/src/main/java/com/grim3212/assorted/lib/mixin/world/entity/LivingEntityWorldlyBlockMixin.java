package com.grim3212.assorted.lib.mixin.world.entity;

import com.grim3212.assorted.lib.core.block.IBlockExtraProperties;
import com.grim3212.assorted.lib.core.block.IBlockSoundType;
import com.grim3212.assorted.lib.core.block.effects.IBlockLandingEffects;
import com.grim3212.assorted.lib.mixin.entity.EntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("InvalidInjectorMethodSignature")
@Mixin(LivingEntity.class)
public abstract class LivingEntityWorldlyBlockMixin extends Entity {
    public LivingEntityWorldlyBlockMixin(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @ModifyVariable(
            method = "travel",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getBlockPosBelowThatAffectsMyMovement()Lnet/minecraft/core/BlockPos;")),
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"), ordinal = 0
    )
    private float assortedlib_rewriteFrictionValueForWorldlyBlocks(float original) {
        if (!(this instanceof EntityAccessor entityAccessor))
            return original;

        final BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement();
        final BlockState blockState = entityAccessor.getLevel().getBlockState(pos);
        if (blockState.getBlock() instanceof IBlockExtraProperties extraProperties) {
            return extraProperties.getFriction(blockState, entityAccessor.getLevel(), pos, this);
        }

        return original;
    }


    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
            method = "playBlockFallSound",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"
            ),
            ordinal = 0
    )
    private SoundType assortedlib_injectGetBlockStateSoundType(final SoundType current) {
        if (!(this instanceof EntityAccessor entityAccessor))
            return current;

        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY() - (double) 0.2F);
        int k = Mth.floor(this.getZ());
        final BlockPos pos = new BlockPos(i, j, k);
        BlockState blockState = entityAccessor.getLevel().getBlockState(pos);

        if (blockState.getBlock() instanceof IBlockSoundType extraProperties) {
            return extraProperties.getSoundType(blockState, entityAccessor.getLevel(), pos, this);
        }
        return current;
    }

    @Inject(
            method = "checkFallDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    protected void assortedlib_checkFallEffects(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci, double d, double e, double f, BlockPos blockPos, float j, double k, int count) {
        if (!(this instanceof EntityAccessor entityAccessor))
            return;

        if (state.getBlock() instanceof IBlockLandingEffects extraProps && extraProps.addLandingEffects(state, (ServerLevel) entityAccessor.getLevel(), pos, state, (LivingEntity) (Object) this, count)) {
            super.checkFallDamage(y, onGround, state, pos);
            ci.cancel();
        }
    }
}
