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
 * <p>
 * The 1.20.1 version of this class had to re-implement the whole emit path: ask the model for its
 * render types, pull the quads out per face and per type, look up a {@code RenderMaterial} for the
 * blend mode and push each quad through a freshly built mesh. None of that is needed - or possible -
 * on 26.2. A model is a {@link BlockStateModel} that collects
 * {@linkplain BlockStateModelPart parts}, the render layer is a property of each quad, and FRAPI's
 * {@code FabricBlockStateModel#emitQuads} is the one hook that receives the level and the position.
 * All this class does, therefore, is fetch the model data for the position and forward the collect
 * call; {@link WrapperBlockStateModel} handles every other method by delegation.
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
     * The model data for the position.
     * <p>
     * {@code RenderAttachedBlockView} is now {@link FabricBlockGetter}, which is injected into
     * {@code BlockGetter} and reads the attachment a {@code RenderDataBlockEntity} published for the
     * chunk being built. Outside of a chunk build there is no attachment, so the block entity is asked
     * directly, exactly as before.
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
