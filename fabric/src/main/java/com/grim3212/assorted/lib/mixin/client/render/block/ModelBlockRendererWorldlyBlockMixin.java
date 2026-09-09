package com.grim3212.assorted.lib.mixin.client.render.block;

import com.grim3212.assorted.lib.core.block.IBlockLightEmission;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Makes a block whose light emission is position dependent still get ambient occlusion when it is
 * emitting nothing at this position.
 * <p>
 * 1.20.1 had to inject before the {@code tesselateWithoutAO} call and re-dispatch to
 * {@code tesselateWithAO} by hand. Both of those are private in 26.2 and the choice between them is
 * a single expression inside {@code tesselateBlock}
 * ({@code this.ambientOcclusion && blockState.getLightEmission() == 0 &&
 * parts.getFirst().useAmbientOcclusion()}), so redirecting the light emission lookup is enough - and
 * it keeps the rest of vanilla's decision intact.
 */
@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererWorldlyBlockMixin {

    @Redirect(
            method = "tesselateBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"
            )
    )
    private int assortedlib_handleWorldlyBlocksWhichDoNotEmitDefaultLightForAO(final BlockState instance, final BlockQuadOutput output, final float x, final float y, final float z, final BlockAndTintGetter level, final BlockPos pos, final BlockState blockState, final BlockStateModel model, final long seed) {
        if (instance.getBlock() instanceof IBlockLightEmission extraProperties) {
            return extraProperties.getLightEmission(instance, level, pos);
        }

        return instance.getLightEmission();
    }
}
