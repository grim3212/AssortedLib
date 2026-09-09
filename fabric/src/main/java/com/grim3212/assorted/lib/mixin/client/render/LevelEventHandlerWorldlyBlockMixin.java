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
 * event.
 * <p>
 * This used to hang off {@code LevelRenderer#levelEvent}. 26.2 pulled the whole level event switch
 * out of {@link net.minecraft.client.renderer.LevelRenderer} - which no longer owns a level, plays
 * no sounds and has no {@code levelEvent} at all - into {@link LevelEventHandler}, so the redirect
 * follows it there. Doing it as a redirect also means the event data (the block state id) no longer
 * has to be smuggled across two injections.
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
