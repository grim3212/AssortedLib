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

/**
 * Makes the default {@code isValidSpawn} predicate consult {@link IBlockLightEmission} so a block
 * whose light level depends on its position still blocks mob spawns correctly.
 * <p>
 * The target is the lambda that initialises {@link BlockBehaviour.Properties#isValidSpawn}, i.e.
 * only blocks that did not set their own predicate. It used to be named by its Yarn intermediary
 * name; 26.x dropped intermediary entirely, so it is named by its real (official) synthetic name
 * {@code lambda$new$4} instead.
 */
@Mixin(BlockBehaviour.Properties.class)
public class BlockBehaviourPropertiesMixin {


    @Inject(method = "lambda$new$4(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z", at = @At("HEAD"), cancellable = true)
    private static void assortedlib_onCallDefaultIsValidSpawnCallback(final BlockState blockState, final BlockGetter blockGetter, final BlockPos position, final EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        if (!(blockState.getBlock() instanceof IBlockLightEmission lightEmission))
            return;

        cir.setReturnValue(blockState.isFaceSturdy(blockGetter, position, Direction.UP) && lightEmission.getLightEmission(blockState, blockGetter, position) < 14);
    }
}
