package com.grim3212.assorted.lib.mixin.world.entity;

import com.grim3212.assorted.lib.core.block.IBlockExtraProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FlyingMob.class)
public abstract class FlyingMobWorldlyBlockMixin extends Mob {
    protected FlyingMobWorldlyBlockMixin(final EntityType<? extends Mob> entityType, final Level level) {
        super(entityType, level);
    }

    @ModifyVariable(
            method = "travel",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
            ),
            ordinal = 0
    )
    private float assortedlib_injectGetFrictionAdaptorForGCalculation(final float current) {
        return assortedlib_handleInjectionPoint(current);
    }

    private float assortedlib_handleInjectionPoint(final float current) {
        final BlockPos pPos = new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ()).below();
        final BlockState blockState = this.level.getBlockState(pPos);

        if (blockState.getBlock() instanceof IBlockExtraProperties extraProperties) {
            return extraProperties.getFriction(blockState, this.level, pPos, this) * 0.91f;
        }
        return current;
    }
}
