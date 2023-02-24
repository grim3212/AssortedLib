package com.grim3212.assorted.lib.mixin.world.entity.item;

import com.grim3212.assorted.lib.core.block.IBlockExtraProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityWorldlyBlockMixin extends Entity {
    public ItemEntityWorldlyBlockMixin(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @ModifyVariable(
            method = "tick",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
            ),
            ordinal = 0
    )
    private float assortedlib_injectGetFrictionAdaptor(final float current) {
        final BlockPos pPos = new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
        final BlockState blockState = this.level.getBlockState(pPos);

        if (blockState.getBlock() instanceof IBlockExtraProperties extraProperties) {
            return extraProperties.getFriction(blockState, this.level, pPos, this) * 0.98f;
        }
        return current;
    }
}
