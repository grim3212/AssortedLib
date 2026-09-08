package com.grim3212.assorted.lib.client.model.baked;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;

/**
 * Defines a model that delegates its operations to another model.
 */
public interface IDelegatingBakedModel extends BlockStateModel {

    /**
     * The model that this model delegates its operations to.
     *
     * @return The delegate.
     */
    BlockStateModel getDelegate();
}
