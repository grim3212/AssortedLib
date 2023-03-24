package com.grim3212.assorted.lib.client.model.data;

import com.grim3212.assorted.lib.platform.ClientServices;

/**
 * Represents a key in model data of a specific type.
 */
public interface IModelDataKey<T> {

    /**
     * Creates a new model data key for the given type.
     * Each returned instance is unique.
     *
     * @param <T> The type of the key.
     * @return The unique model data key.
     */
    static <T> IModelDataKey<T> create() {
        return ClientServices.MODELS.createNewModelDataKey();
    }
}
