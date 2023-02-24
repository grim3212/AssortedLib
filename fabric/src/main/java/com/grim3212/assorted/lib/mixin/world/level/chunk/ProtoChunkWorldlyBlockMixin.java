package com.grim3212.assorted.lib.mixin.world.level.chunk;

import com.grim3212.assorted.lib.core.block.IBlockExtraProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkWorldlyBlockMixin extends ChunkAccess implements BlockGetter {

    @Shadow
    @Final
    private List<BlockPos> lights;
    @Shadow
    private volatile ChunkStatus status;
    @Shadow
    @Nullable
    private volatile LevelLightEngine lightEngine;

    public ProtoChunkWorldlyBlockMixin(final ChunkPos chunkPos, final UpgradeData upgradeData, final LevelHeightAccessor levelHeightAccessor, final Registry<Biome> registry, final long l, @Nullable final LevelChunkSection[] levelChunkSections, @Nullable final BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
    }

    @Inject(
            method = "setBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I",
                    ordinal = 0
            )
    )
    public void assortedlib_onSetBlockStateAddCustomLights(final BlockPos pos, final BlockState state, final boolean isMoving, final CallbackInfoReturnable<BlockState> cir) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        if (state.getBlock() instanceof IBlockExtraProperties extraProperties && extraProperties.getLightEmission(state, this, pos) > 0) {
            this.lights.add(new BlockPos((i & 15) + this.getPos().getMinBlockX(), j, (k & 15) + this.getPos().getMinBlockZ()));
        }
    }

    @ModifyVariable(
            method = "setBlockState",
            at = @At(
                    value = "STORE"
            ),
            index = 9)
    public BlockState assortedlib_onSetBlockStateDoLightEngineUpdate(final BlockState blockState, BlockPos pos, BlockState state, boolean isMoving) {
        if (state.getLightEmission() == blockState.getLightEmission() && (state.getBlock() instanceof IBlockExtraProperties || blockState.getBlock() instanceof IBlockExtraProperties)) {
            int newBlockEmissions = state.getLightEmission();
            if (state.getBlock() instanceof IBlockExtraProperties newExtraProperties) {
                newBlockEmissions = newExtraProperties.getLightEmission(state, this, pos);
            }

            int oldBlockEmissions = blockState.getLightEmission();
            if (blockState.getBlock() instanceof IBlockExtraProperties oldExtraProperties) {
                oldBlockEmissions = oldExtraProperties.getLightEmission(state, this, pos);
            }

            if (this.status.isOrAfter(ChunkStatus.FEATURES) && state != blockState && (state.getLightBlock(this, pos) != blockState.getLightBlock(this, pos) || newBlockEmissions != oldBlockEmissions || state.useShapeForLightOcclusion() || blockState.useShapeForLightOcclusion())) {
                if (this.lightEngine != null) {
                    this.lightEngine.checkBlock(pos);
                }
            }
        }

        return blockState;
    }
}
