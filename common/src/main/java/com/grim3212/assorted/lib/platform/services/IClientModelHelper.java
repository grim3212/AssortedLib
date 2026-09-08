package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.client.model.data.IModelDataKey;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
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

    /**
     * Creates a new model data key for the given type.
     * Each returned instance is unique.
     *
     * @param <T> The type of the key.
     * @return The unique model data key.
     */
    @NotNull <T> IModelDataKey<T> createNewModelDataKey();

    /**
     * Gets the model for the given resource location.
     *
     * @param unbakedModel The resource location of the model.
     * @return The model.
     */
    UnbakedModel getUnbakedModel(final Identifier unbakedModel);

    /**
     * Registers a model specification loader for the given resource location.
     *
     * @param name        the name of the loader
     * @param modelLoader the loader.
     */


    /**
     * Adapts a given block state model to the current platform.
     * Adaptation might not be necessary in all cases, but this method allows the underlying platform to adapt a default vanilla {@link BlockStateModel},
     * to platform specific implementations, unlocking additional functionality.
     *
     * @param model The model to adapt.
     * @return The adapted model.
     */
    BlockStateModel adaptToPlatform(final BlockStateModel model);

    /**
     * Indicates if the blockstate needs to be rendered in the render type.
     *
     * @param blockState The block state in question.
     * @param renderType The render type.
     * @return True when rendering in the given render type is required, false when not.
     */
    boolean canRenderInType(final BlockState blockState, final RenderType renderType);

    /**
     * Indicates if the fluidState needs to be rendered in the render type.
     *
     * @param fluidState The fluid state in question.
     * @param renderType The render type.
     * @return True when rendering in the given render type is required, false when not.
     */
    boolean canRenderInType(final FluidState fluidState, final RenderType renderType);

    /**
     * Retrieves the {@linkplain RenderType render types} for the given block state, data and model.
     *
     * @param model The model to get the types for.
     * @param state The block state to get the types for.
     * @param rand  The random source to use.
     * @param data  The data to use.
     * @return The render types for the given block state, data and model.
     */
    @NotNull
    Collection<RenderType> getRenderTypesFor(BlockStateModel model, BlockState state, RandomSource rand, IBlockModelData data);

    // TODO(26.2): the item-side getRenderTypesFor(model, stack, isFabulous) has no equivalent and is
    //  removed rather than stubbed.
    //  What it used to do: asked a BakedModel which RenderTypes it would draw an ItemStack in, so
    //  callers could pre-sort or re-render those passes themselves.
    //  Why it cannot be expressed: item rendering is push-only now. An ItemModel does not report its
    //  render types; ItemModel#update mutates an ItemStackRenderState, and each
    //  ItemStackRenderState.LayerRenderState carries its own RenderType internally as a write-only
    //  sink. The render types therefore only exist for the duration of one update() call. A caller
    //  that genuinely needs them must run update() against its own ItemStackRenderState and read the
    //  resulting layers, which is a different shape and belongs at the call site. "Fabulous" is also
    //  no longer a per-item rendering distinction.

    RenderType getItemUnlitUnsortedTranslucentRenderType();

    RenderType getItemUnsortedTranslucentRenderType();
}
