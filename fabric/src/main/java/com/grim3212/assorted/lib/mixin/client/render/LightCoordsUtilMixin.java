package com.grim3212.assorted.lib.mixin.client.render;

import com.grim3212.assorted.lib.core.block.IBlockLightEmission;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Feeds the position dependent {@link IBlockLightEmission} value into the packed light coordinates a
 * block is drawn with.
 * <p>
 * The 1.20.1 hook was {@code LevelRenderer#getLightColor(BlockAndTintGetter, BlockState, BlockPos)}.
 * {@link net.minecraft.client.renderer.LevelRenderer} does not compute light at all in 26.2 - the
 * whole packing lives in {@link LightCoordsUtil#getLightCoords(LightCoordsUtil.BrightnessGetter,
 * BlockAndLightGetter, BlockState, BlockPos)}, which is what {@code BlockModelLighter} and every
 * other call site goes through - so the same redirect is applied there instead.
 */
@Mixin(LightCoordsUtil.class)
public abstract class LightCoordsUtilMixin {

    @Redirect(
            method = "getLightCoords(Lnet/minecraft/util/LightCoordsUtil$BrightnessGetter;Lnet/minecraft/world/level/BlockAndLightGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"
            )
    )
    private static int assortedlib_injectGetBlockStateLightEmission(final BlockState instance, final LightCoordsUtil.BrightnessGetter brightnessGetter, final BlockAndLightGetter level, final BlockState state, final BlockPos pos) {
        if (instance.getBlock() instanceof IBlockLightEmission extraProperties) {
            return extraProperties.getLightEmission(instance, level, pos);
        }

        return instance.getLightEmission();
    }
}
