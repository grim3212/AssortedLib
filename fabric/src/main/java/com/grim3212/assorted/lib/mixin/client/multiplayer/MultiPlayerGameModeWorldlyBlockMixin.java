package com.grim3212.assorted.lib.mixin.client.multiplayer;

import com.grim3212.assorted.lib.core.block.IBlockExtraProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeWorldlyBlockMixin {

    @ModifyVariable(
            method = "continueDestroyBlock",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"
            ),
            ordinal = 0
    )
    private SoundType assortedlib_injectGetBlockStateSoundType(final SoundType current, BlockPos pPosBlock, Direction pDirectionFacing) {
        final BlockState blockState = Minecraft.getInstance().level.getBlockState(pPosBlock);
        if (blockState.getBlock() instanceof IBlockExtraProperties extraProperties) {
            return extraProperties.getSoundType(blockState, Minecraft.getInstance().level, pPosBlock, Minecraft.getInstance().player);
        }
        return current;
    }
}
