package com.grim3212.assorted.lib.mixin.world.entity.vehicle;

import com.grim3212.assorted.lib.core.block.IBlockExtraProperties;
import com.grim3212.assorted.lib.mixin.entity.EntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = Boat.class, priority = Integer.MIN_VALUE)
public abstract class BoatWorldlyBlockMixin extends Entity {

    @Unique
    private BlockState workingState;
    @Unique
    private BlockPos workingPos;

    public BoatWorldlyBlockMixin(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }


    @Inject(
            method = "getGroundFriction",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void assortedlib_injectGetFrictionAdaptorForPosition(final CallbackInfoReturnable<Float> cir, final AABB aABB, final AABB aABB2, final int i, final int j, final int k, final int l, final int m, final int n, final VoxelShape voxelShape, final float f, final int o, final BlockPos.MutableBlockPos mutableBlockPos) {
        this.workingPos = mutableBlockPos;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
            method = "getGroundFriction",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            ),
            ordinal = 0
    )
    private BlockState assortedlib_injectGetFrictionAdaptorForState(final BlockState current) {
        this.workingState = current;
        return current;
    }

    @ModifyVariable(
            method = "getGroundFriction",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
            ),
            ordinal = 0
    )
    private float assortedlib_injectGetFrictionAdaptorForState(final float current) {
        if (!(this instanceof EntityAccessor entityAccessor))
            return current;

        if (this.workingState.getBlock() instanceof IBlockExtraProperties extraProperties) {
            return extraProperties.getFriction(this.workingState, entityAccessor.getLevel(), this.workingPos, this);
        }

        return current;
    }
}
