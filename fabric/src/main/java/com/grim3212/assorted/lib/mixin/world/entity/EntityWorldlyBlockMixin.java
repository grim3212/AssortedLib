package com.grim3212.assorted.lib.mixin.world.entity;

import com.grim3212.assorted.lib.core.block.IBlockSoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityWorldlyBlockMixin {

    @Shadow
    public Level level;

    @Shadow
    public abstract Level getLevel();

    @Shadow
    public abstract void playSound(final SoundEvent sound, final float volume, final float pitch);

    private Entity getThis() {
        return (Entity) (Object) this;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
            method = "playStepSound",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void assortedlib_redirectGetOffsetBlockStateSoundType(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (!state.getMaterial().isLiquid()) {
            BlockState blockState = this.level.getBlockState(pos.above());
            blockState = blockState.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS) ? blockState : state;

            if (blockState.getBlock() instanceof IBlockSoundType extraProperties) {
                SoundType soundType = extraProperties.getSoundType(blockState, this.level, pos, this.getThis());
                this.playSound(soundType.getStepSound(), soundType.getVolume() * 0.15F, soundType.getPitch());
                ci.cancel();
            }
        }
    }
}
