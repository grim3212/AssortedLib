package com.grim3212.assorted.lib.mixin.client.render.block;

import com.grim3212.assorted.lib.core.block.IBlockLightEmission;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererWorldlyBlockMixin {
    @Shadow
    public abstract void tesselateWithAO(final BlockAndTintGetter p_234391_, final BakedModel p_234392_, final BlockState p_234393_, final BlockPos p_234394_, final PoseStack p_234395_, final VertexConsumer p_234396_, final boolean p_234397_, final RandomSource p_234398_, final long p_234399_, final int p_234400_);

    @Inject(
            method = "tesselateBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateWithoutAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JI)V"
            ),
            cancellable = true)
    private void assortedlib_handleWorldlyBlocksWhichDoNotEmitDefaultLightForAO(final BlockAndTintGetter blockAndTintGetter, final BakedModel bakedModel, final BlockState blockState, final BlockPos blockPos, final PoseStack poseStack, final VertexConsumer vertexConsumer, final boolean bl, final RandomSource randomSource, final long l, final int i, final CallbackInfo ci) {
        if (blockState.getBlock() instanceof IBlockLightEmission extraProperties) {
            boolean usesAmbientOcclusion = Minecraft.useAmbientOcclusion() && extraProperties.getLightEmission(blockState, blockAndTintGetter, blockPos) == 0 && bakedModel.useAmbientOcclusion();
            if (usesAmbientOcclusion) {
                this.tesselateWithAO(blockAndTintGetter, bakedModel, blockState, blockPos, poseStack, vertexConsumer, bl, randomSource, l, i);
                ci.cancel();
            }
        }
    }
}
