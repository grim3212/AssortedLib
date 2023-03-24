package com.grim3212.assorted.lib.core.block;

import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a block entity which has its own model data.
 */
public interface IBlockEntityWithModelData {
    /**
     * The model data of the block entity.
     *
     * @return The model data of the block entity.
     */
    @NotNull
    IBlockModelData getBlockModelData();
}

