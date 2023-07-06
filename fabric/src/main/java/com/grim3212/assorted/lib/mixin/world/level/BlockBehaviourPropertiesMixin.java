package com.grim3212.assorted.lib.mixin.world.level;

import com.grim3212.assorted.lib.core.block.IBlockLightEmission;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.Properties.class)
public class BlockBehaviourPropertiesMixin {


    @Inject(method = "method_26239", remap = false, at = @At("HEAD"), cancellable = true)
    private static void assortedlib_onCallDefaultIsValidSpawnCallback(final BlockState blockState, final BlockGetter blockGetter, final BlockPos position, final EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        if (!(blockState.getBlock() instanceof IBlockLightEmission lightEmission))
            return;

        cir.setReturnValue(blockState.isFaceSturdy(blockGetter, position, Direction.UP) && lightEmission.getLightEmission(blockState, blockGetter, position) < 14);
    }
}
