package com.grim3212.assorted.lib.mixin.block;

import com.grim3212.assorted.lib.core.block.IPlantSustainable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets an {@link IPlantSustainable} soil block decide whether a plant may be placed on it.
 * <p>
 * The 1.20.1 hook was {@code BushBlock#mayPlaceOn}, which every plant inherited. 26.2 split the
 * class: {@link net.minecraft.world.level.block.BushBlock} is now a concrete flower-pot style block
 * and the shared plant behaviour - {@code mayPlaceOn} included - lives on
 * {@link VegetationBlock}, so the injection follows it there and still covers every plant.
 */
@Mixin(VegetationBlock.class)
public class VegetationBlockMixin {
    @Inject(method = "mayPlaceOn(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    public void assortedlib_mayPlaceOn(BlockState state, BlockGetter blockGetter, BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (state.getBlock() instanceof IPlantSustainable planter) {
            callbackInfo.setReturnValue(planter.canSustainPlant(state, blockGetter, pos, Direction.UP, (VegetationBlock) (Object) this));
        }
    }
}
