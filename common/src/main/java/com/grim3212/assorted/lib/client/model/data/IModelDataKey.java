package com.grim3212.assorted.lib.client.model.data;

import com.grim3212.assorted.lib.platform.ClientServices;

/**
 * Represents a key in model data of a specific type.
 */
public interface IModelDataKey<T> {

    /** Creates a new model data key; every call returns a unique instance. */
    static <T> IModelDataKey<T> create() {
        return ClientServices.MODELS.createNewModelDataKey();
    }
}
