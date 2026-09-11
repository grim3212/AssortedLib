package com.grim3212.assorted.lib.mixin.client.render;

import com.grim3212.assorted.lib.core.block.IBlockSoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Plays the {@link IBlockSoundType} sound instead of the baked in one for the block break level
 * event. A redirect, so the event data (the block state id) is at hand in a single injection.
 */
@Mixin(LevelEventHandler.class)
public abstract class LevelEventHandlerWorldlyBlockMixin {

    @Shadow
    @Final
    private ClientLevel level;

    @Redirect(
            method = "levelEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"
            )
    )
    public SoundType assortedlib_redirectGetBlockStateSoundType(final BlockState blockState, final int type, final BlockPos pos, final int data) {
        if (blockState.getBlock() instanceof IBlockSoundType extraProperties) {
            return extraProperties.getSoundType(blockState, this.level, pos, Minecraft.getInstance().getCameraEntity());
        }

        return blockState.getSoundType();
    }
}
