package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.baked.IDelegatingBakedModel;
import com.grim3212.assorted.lib.client.model.data.ForgeBlockModelDataPlatformDelegate;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Bridges the loader agnostic {@link IDataAwareBakedModel} onto NeoForge's own position aware model
 * hook.
 * <p>
 * {@code BakedModel} is gone; a block model is a {@link BlockStateModel} which is baked per block
 * state and hands its geometry over as {@linkplain BlockStateModelPart parts}. NeoForge re-adds the
 * level/position context on {@link DynamicBlockStateModel}, which is where the model data is read
 * from ({@code level.getModelData(pos)}), so this delegate translates that into the
 * {@link IBlockModelData} the common side works with.
 * <p>
 * TODO(26.2): the item side of this delegate is gone. It used to also implement
 *  {@code ICompoundItemBakedModel} and forward {@code getRenderPasses} / {@code getQuads(ItemStack,
 *  ...)} / {@code getRenderTypes(ItemStack, boolean)} / {@code applyTransform}. Item rendering is
 *  push-only now - an {@code ItemModel} mutates an {@code ItemStackRenderState} and hands nothing
 *  back - and {@code BlockStateModel} and {@code ItemModel} are unrelated types, so a single wrapper
 *  can no longer cover both. Per render type filtering ({@code ChunkRenderTypeSet}, which has no
 *  NeoForge equivalent) is gone too: every {@link BakedQuad} carries its own material info and the
 *  section compiler buckets quads by it.
 */
public final class ForgeBakedModelDelegate implements DynamicBlockStateModel, IDelegatingBakedModel, IDataAwareBakedModel {

    private final BlockStateModel delegate;

    public ForgeBakedModelDelegate(final BlockStateModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public BlockStateModel getDelegate() {
        return this.delegate;
    }

    @Override
    public void collectParts(final BlockAndTintGetter level, final BlockPos pos, final BlockState state, final RandomSource random, final List<BlockStateModelPart> parts) {
        if (this.delegate instanceof IDataAwareBakedModel dataAwareBakedModel) {
            dataAwareBakedModel.collectParts(random, new ForgeBlockModelDataPlatformDelegate(level.getModelData(pos)), parts);
            return;
        }

        this.delegate.collectParts(level, pos, state, random, parts);
    }

    @Override
    public void collectParts(final @NotNull RandomSource random, final @NotNull IBlockModelData extraData, final @NotNull List<BlockStateModelPart> parts) {
        if (this.delegate instanceof IDataAwareBakedModel dataAwareBakedModel) {
            dataAwareBakedModel.collectParts(random, extraData, parts);
            return;
        }

        // This overload carries no level or position, so hand the delegate the same empty context
        // DynamicBlockStateModel feeds the level aware overload with.
        this.delegate.collectParts(BlockAndTintGetter.EMPTY, BlockPos.ZERO, Blocks.AIR.defaultBlockState(), random, parts);
    }

    // Deprecated by NeoForge in favour of the level aware overload above; it still has to be implemented
    // here to pick a winner between DynamicBlockStateModel's and IDataAwareBakedModel's defaults.
    @Override
    @Deprecated
    public void collectParts(final @NotNull RandomSource random, final @NotNull List<BlockStateModelPart> parts) {
        this.delegate.collectParts(random, parts);
    }

    @Override
    public Object createGeometryKey(final BlockAndTintGetter level, final BlockPos pos, final BlockState state, final RandomSource random) {
        // The delegate may be data aware, in which case its geometry depends on the block entity data
        // this delegate feeds it and cannot be keyed on the block state alone.
        return this.delegate instanceof IDataAwareBakedModel ? null : this.delegate.createGeometryKey(level, pos, state, random);
    }

    @Override
    @Deprecated
    public Material.Baked particleMaterial() {
        return this.delegate.particleMaterial();
    }

    @Override
    public Material.Baked particleMaterial(final BlockAndTintGetter level, final BlockPos pos, final BlockState state) {
        if (this.delegate instanceof IDataAwareBakedModel dataAwareBakedModel) {
            return dataAwareBakedModel.particleMaterial(new ForgeBlockModelDataPlatformDelegate(level.getModelData(pos)));
        }

        return this.delegate.particleMaterial(level, pos, state);
    }

    @Override
    public Material.Baked particleMaterial(final @NotNull IBlockModelData extraData) {
        if (this.delegate instanceof IDataAwareBakedModel dataAwareBakedModel) {
            return dataAwareBakedModel.particleMaterial(extraData);
        }

        // No level or position here; same empty context the collectParts overload above uses.
        return this.delegate.particleMaterial(BlockAndTintGetter.EMPTY, BlockPos.ZERO, Blocks.AIR.defaultBlockState());
    }

    @Override
    @Deprecated
    public @BakedQuad.MaterialFlags int materialFlags() {
        return this.delegate.materialFlags();
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags(final BlockAndTintGetter level, final BlockPos pos, final BlockState state) {
        return this.delegate.materialFlags(level, pos, state);
    }

    /**
     * Convenience for call sites that still hold a raw {@link ModelData}.
     */
    public void collectParts(final RandomSource random, final ModelData data, final List<BlockStateModelPart> parts) {
        collectParts(random, new ForgeBlockModelDataPlatformDelegate(data), parts);
    }
}
