package com.grim3212.assorted.lib.mixin.world.entity.animal.horse;

import com.grim3212.assorted.lib.core.block.IBlockSoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseWorldlyBlockMixin extends Entity {

    public AbstractHorseWorldlyBlockMixin(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @ModifyVariable(
            method = "playStepSound",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"
            ),
            ordinal = 0
    )
    private SoundType assortedlib_injectGetBlockStateSoundType(final SoundType current, BlockPos pPos, BlockState pBlock) {
        if (pBlock.getBlock() instanceof IBlockSoundType extraProperties) {
            return extraProperties.getSoundType(pBlock, this.level, pPos, this);
        }
        return current;
    }
}
