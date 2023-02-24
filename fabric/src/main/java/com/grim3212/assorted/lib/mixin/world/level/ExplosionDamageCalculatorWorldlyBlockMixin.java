package com.grim3212.assorted.lib.mixin.world.level;

import com.grim3212.assorted.lib.core.block.IBlockExtraProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ExplosionDamageCalculator.class)
public abstract class ExplosionDamageCalculatorWorldlyBlockMixin {

    @Inject(
            method = "getBlockExplosionResistance",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void assortedlib_handleExplosionResistanceOnWorldlyBlocks(final Explosion explosion, final BlockGetter reader, final BlockPos pos, final BlockState state, final FluidState fluid, final CallbackInfoReturnable<Optional<Float>> cir) {
        if (state.getBlock() instanceof IBlockExtraProperties extraProperties) {
            cir.setReturnValue(state.isAir() && fluid.isEmpty() ? Optional.empty() : Optional.of(Math.max(extraProperties.getExplosionResistance(state, reader, pos, explosion), fluid.getExplosionResistance())));
        }
    }
}
