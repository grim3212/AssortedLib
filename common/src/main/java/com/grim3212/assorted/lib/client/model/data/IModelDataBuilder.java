package com.grim3212.assorted.lib.client.model.data;

import com.grim3212.assorted.lib.platform.ClientServices;

/**
 * Builder for a new model data instances.
 */
public interface IModelDataBuilder {

    /** Creates a new, empty builder. */
    static IModelDataBuilder create() {
        return ClientServices.MODELS.createNewModelDataBuilder();
    }

    /**
     * Builds the new model data instance from the builders current configuration.
     *
     * @return The model data from the current setup.
     */
    IBlockModelData build();

    /** Sets the initial value for {@code key}, returning this builder. */
    <T> IModelDataBuilder withInitial(IModelDataKey<T> key, T value);
}
