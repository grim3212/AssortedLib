package com.grim3212.assorted.lib.client.model.baked;

import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A {@link BlockStateModel} whose geometry additionally depends on the model data a block entity
 * exposes for its position.
 * <p>
 * In 26.2 a {@link BlockStateModel} is baked <em>per {@link BlockState}</em> (see
 * {@link BlockStateModel.UnbakedRoot#bake(BlockState, net.minecraft.client.resources.model.ModelBaker)}),
 * so the block state no longer has to be passed in at render time - capture it while baking. What
 * vanilla does <em>not</em> provide is the position dependent part: its only entry point is
 * {@link BlockStateModel#collectParts(RandomSource, List)}, which receives no level, no position and
 * no model data. This interface re-adds that parameter so the loader specific chunk renderer bridge
 * (see the {@code FabricBakedModelDelegate} / {@code ForgeBakedModelDelegate} classes in the loader
 * modules) can route the data through; plain vanilla call sites fall through to the default
 * implementation with {@linkplain IBlockModelData#empty() empty} data.
 */
public interface IDataAwareBakedModel extends BlockStateModel {

    /**
     * Collects the parts of this model for the given data.
     *
     * @param random    The random source, seeded from the block position.
     * @param extraData The data to use.
     * @param output    The list the parts are appended to.
     */
    void collectParts(@NotNull RandomSource random, @NotNull IBlockModelData extraData, @NotNull List<BlockStateModelPart> output);

    @Override
    default void collectParts(@NotNull RandomSource random, @NotNull List<BlockStateModelPart> output) {
        collectParts(random, IBlockModelData.empty(), output);
    }

    // TODO(26.2): the old getSupportedRenderTypes(BlockState, RandomSource, IBlockModelData) and
    //  getSupportedRenderTypes(ItemStack, boolean) pair is gone. A model no longer picks a RenderType
    //  at all: every BakedQuad carries a BakedQuad.MaterialInfo whose layer() is a ChunkSectionLayer
    //  derived from the sprite's Transparency (or Material#forceTranslucent), and the section
    //  compiler buckets quads by that. There is nothing left for a model to answer, and no
    //  "fabulous" item variant either, so the concept was dropped rather than faked.
}
