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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("InvalidInjectorMethodSignature")
@Mixin(LivingEntity.class)
public abstract class LivingEntityWorldlyBlockMixin extends Entity {
    public LivingEntityWorldlyBlockMixin(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    // 26.2 split LivingEntity.travel into travelInFluid / travelFallFlying / travelInAir, and the
    // ground friction is no longer stored straight out of Block.getFriction() - it is fed into
    // computeModifiedFriction(F, F) with the FRICTION_MODIFIER attribute, so there is no local to
    // modify at that point any more. Redirecting the getFriction() call keeps the attribute pass
    // and matches what the old INVOKE_ASSIGN capture did.
    @Redirect(
            method = "travelInAir",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
    )
    private float assortedlib_rewriteFrictionValueForWorldlyBlocks(Block block) {
        if (!(this instanceof EntityAccessor entityAccessor))
            return block.getFriction();

        final BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement();
        final BlockState blockState = entityAccessor.getLevel().getBlockState(pos);
        if (blockState.getBlock() instanceof IBlockExtraProperties extraProperties) {
            return extraProperties.getFriction(blockState, entityAccessor.getLevel(), pos, this);
        }

        return block.getFriction();
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
    // 26.2 LVT of LivingEntity.checkFallDamage at the sendParticles call, in slot order:
    // 6 ServerLevel level, 7 double power, 9 double x, 11 double y, 13 double z,
    // 15 BlockPos entityPos, 16 double scale, 18 int particles.
    protected void assortedlib_checkFallEffects(double ya, boolean onGround, BlockState onState, BlockPos pos, CallbackInfo ci, ServerLevel level, double power, double x, double y, double z, BlockPos entityPos, double scale, int particles) {
        if (onState.getBlock() instanceof IBlockLandingEffects extraProps && extraProps.addLandingEffects(onState, level, pos, onState, (LivingEntity) (Object) this, particles)) {
            super.checkFallDamage(ya, onGround, onState, pos);
            ci.cancel();
        }
    }
}
