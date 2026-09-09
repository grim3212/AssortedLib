package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.client.model.FabricUnbakedModelTracker;
import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.*;
import com.grim3212.assorted.lib.client.model.loader.FabricBakedModelDelegate;
import com.grim3212.assorted.lib.platform.services.IClientModelHelper;
import net.fabricmc.fabric.api.client.renderer.v1.render.ChunkSectionLayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FabricClientModelHelper implements IClientModelHelper {

    /**
     * The seed the render type probes bake their geometry with. Model parts are collected per random
     * source in 26.x, so a stable seed keeps the answer stable across calls.
     */
    private static final long PROBE_SEED = 42L;

    @Override
    public void requestModelDataRefresh(BlockEntity blockEntity) {
        // NO-OP
    }

    private static final IBlockModelData EMPTY = new ModelDataBuilder().build();

    @Override
    public IBlockModelData empty() {
        return EMPTY;
    }

    @Override
    public @NotNull IModelDataBuilder createNewModelDataBuilder() {
        return new ModelDataBuilder();
    }

    @Override
    public @NotNull <T> IModelDataKey<T> createNewModelDataKey() {
        return new ModelDataKey<>();
    }

    @Override
    public UnbakedModel getUnbakedModel(Identifier unbakedModel) {
        return FabricUnbakedModelTracker.getUnbakedModel(unbakedModel);
    }

    @Override
    public BlockStateModel adaptToPlatform(BlockStateModel model) {
        if (model instanceof FabricBakedModelDelegate) {
            return model;
        }

        return new FabricBakedModelDelegate(model);
    }

    // ItemBlockRenderTypes is gone and nothing maps a block state to a layer any more, so this has to
    // probe the block's baked model. That is more work than the old map lookup was; callers on a hot
    // path should hold on to the answer rather than asking per frame.
    @Override
    public boolean canRenderInType(final BlockState blockState, final RenderType renderType) {
        final BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
        return getRenderTypesFor(model, blockState, RandomSource.create(PROBE_SEED), IBlockModelData.empty()).contains(renderType);
    }

    @Override
    public boolean canRenderInType(final FluidState fluidState, final RenderType renderType) {
        // A fluid's layer is baked into its FluidModel rather than answered by ItemBlockRenderTypes.
        return ChunkSectionLayerHelper.getMovingBlockRenderType(
                Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState).layer()) == renderType;
    }

    /**
     * A model no longer declares the render types it draws in; every {@link BakedQuad} carries the
     * {@code ChunkSectionLayer} it was baked for. The types are therefore collected from the quads the
     * model actually produces for the given data.
     */
    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(final BlockStateModel model, final BlockState state, final RandomSource rand, final IBlockModelData data) {
        final List<BlockStateModelPart> parts = new ArrayList<>();
        if (model instanceof IDataAwareBakedModel dataAwareModel) {
            dataAwareModel.collectParts(rand, data, parts);
        } else {
            model.collectParts(rand, parts);
        }

        final Set<RenderType> renderTypes = new LinkedHashSet<>();
        for (BlockStateModelPart part : parts) {
            collectRenderTypes(part.getQuads(null), renderTypes);
            for (Direction direction : Direction.values()) {
                collectRenderTypes(part.getQuads(direction), renderTypes);
            }
        }

        return renderTypes;
    }

    private static void collectRenderTypes(final List<BakedQuad> quads, final Set<RenderType> output) {
        for (BakedQuad quad : quads) {
            output.add(ChunkSectionLayerHelper.getMovingBlockRenderType(quad.materialInfo().layer()));
        }
    }

    // RenderType.solid() is gone; the standalone (non chunk) equivalent of the old solid block layer
    // is the moving block type. Fabric has no unlit or unsorted translucent variants of its own, so
    // both of these keep answering the same type they did before.
    @Override
    public RenderType getItemUnlitUnsortedTranslucentRenderType() {
        return RenderTypes.solidMovingBlock();
    }

    @Override
    public RenderType getItemUnsortedTranslucentRenderType() {
        return RenderTypes.solidMovingBlock();
    }
}
