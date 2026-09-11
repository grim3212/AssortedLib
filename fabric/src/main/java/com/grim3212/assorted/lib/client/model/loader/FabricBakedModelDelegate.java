package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.baked.IDelegatingBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.core.block.IBlockEntityWithModelData;
import net.fabricmc.fabric.api.blockgetter.v2.FabricBlockGetter;
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModelPart;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Routes the block entity model data of an {@link IDataAwareBakedModel} into the Fabric renderer.
 * FRAPI's {@code FabricBlockStateModel#emitQuads} is the one hook with the level and position, so
 * this fetches the data there and forwards the collect call; {@link WrapperBlockStateModel}
 * delegates the rest.
 */
public class FabricBakedModelDelegate extends WrapperBlockStateModel implements IDelegatingBakedModel {

    public FabricBakedModelDelegate(final BlockStateModel delegate) {
        super(delegate);
    }

    @Override
    public BlockStateModel getDelegate() {
        return this.wrapped;
    }

    @Override
    public void emitQuads(final QuadEmitter emitter, final BlockAndTintGetter blockView, final BlockPos pos, final BlockState state, final RandomSource random, final Predicate<Direction> cullTest) {
        if (!(getDelegate() instanceof final IDataAwareBakedModel dataAwareBakedModel)) {
            super.emitQuads(emitter, blockView, pos, state, random, cullTest);
            return;
        }

        final List<BlockStateModelPart> parts = new ArrayList<>();
        dataAwareBakedModel.collectParts(random, getBlockModelData(blockView, pos), parts);

        for (final BlockStateModelPart part : parts) {
            // FRAPI injects FabricBlockStateModelPart into BlockStateModelPart at runtime; the cast is
            // how it is reached from code that is not compiled against the injected interfaces.
            ((FabricBlockStateModelPart) part).emitQuads(emitter, cullTest);
        }
    }

    /**
     * {@link WrapperBlockStateModel}'s own override forwards to the wrapped model, which would answer
     * the sprite its model json named rather than the one the block entity's data selects.
     */
    @Override
    public Material.Baked particleMaterial(final BlockAndTintGetter blockView, final BlockPos pos, final BlockState state) {
        if (getDelegate() instanceof final IDataAwareBakedModel dataAwareBakedModel) {
            return dataAwareBakedModel.particleMaterial(getBlockModelData(blockView, pos));
        }

        return super.particleMaterial(blockView, pos, state);
    }

    /**
     * The model data for the position: during a chunk build, the attachment a {@code
     * RenderDataBlockEntity} published, read through {@link FabricBlockGetter}; otherwise the block
     * entity is asked directly.
     */
    private static IBlockModelData getBlockModelData(final BlockAndTintGetter blockView, final BlockPos pos) {
        final Object attachmentData = blockView instanceof final FabricBlockGetter fabricBlockGetter ? fabricBlockGetter.getBlockEntityRenderData(pos) : null;
        if (attachmentData instanceof final IBlockModelData blockModelDataAttachment) {
            return blockModelDataAttachment;
        }

        final BlockEntity blockEntity = blockView.getBlockEntity(pos);
        if (blockEntity instanceof final IBlockEntityWithModelData blockEntityWithModelData) {
            return blockEntityWithModelData.getBlockModelData();
        }

        return IBlockModelData.empty();
    }
}
