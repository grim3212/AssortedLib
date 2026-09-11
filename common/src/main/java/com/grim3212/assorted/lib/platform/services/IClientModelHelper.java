package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.client.model.data.IModelDataKey;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface IClientModelHelper {
    /**
     * Refreshes the model data for the block entity.
     *
     * @param blockEntity The block entity to refresh the model data for.
     */
    void requestModelDataRefresh(BlockEntity blockEntity);

    /**
     * Returns an empty block model data to use in cases where it is not available.
     *
     * @return The block model data.
     */
    IBlockModelData empty();

    /**
     * Creates a new model data builder instance.
     *
     * @return The model data instance.
     */
    @NotNull
    IModelDataBuilder createNewModelDataBuilder();

    /** Creates a new model data key; each call returns a unique key. */
    @NotNull <T> IModelDataKey<T> createNewModelDataKey();

    /** The unbaked model at the given location. */
    UnbakedModel getUnbakedModel(final Identifier unbakedModel);

    /**
     * Adapts a vanilla {@link BlockStateModel} to this loader's own model implementation, unlocking
     * loader-specific functionality where there is any.
     */
    BlockStateModel adaptToPlatform(final BlockStateModel model);

    /** Whether the block state has to be drawn in the given render type. */
    boolean canRenderInType(final BlockState blockState, final RenderType renderType);

    /** Whether the fluid state has to be drawn in the given render type. */
    boolean canRenderInType(final FluidState fluidState, final RenderType renderType);

    /** The {@linkplain RenderType render types} the model draws this block state and data in. */
    @NotNull
    Collection<RenderType> getRenderTypesFor(BlockStateModel model, BlockState state, RandomSource rand, IBlockModelData data);

    // TODO(26.2): the item-side getRenderTypesFor(model, stack, isFabulous) was removed, not
    //  stubbed: item rendering is push-only, so an ItemModel's render types exist only inside one
    //  ItemModel#update. A caller that needs them runs update() on its own ItemStackRenderState
    //  and reads the layers.

    RenderType getItemUnlitUnsortedTranslucentRenderType();

    RenderType getItemUnsortedTranslucentRenderType();
}
