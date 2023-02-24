package com.grim3212.assorted.lib.mixin.block;

import com.grim3212.assorted.lib.core.block.IPlantSustainable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin {
    @Inject(method = "canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    public void assortedlib_canSurvive(BlockState state, LevelReader levelReader, BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (levelReader.getBlockState(pos.below()).getBlock() instanceof IPlantSustainable planter) {
            BlockPos planterPos = pos.below();
            callbackInfo.setReturnValue(planter.canSustainPlant(state, levelReader, planterPos, Direction.UP, (SugarCaneBlock) (Object) this));
        }
    }
}
