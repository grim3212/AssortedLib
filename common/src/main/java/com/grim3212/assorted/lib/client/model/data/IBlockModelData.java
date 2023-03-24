package com.grim3212.assorted.lib.client.model.data;

import com.grim3212.assorted.lib.platform.ClientServices;
import org.jetbrains.annotations.Nullable;

public interface IBlockModelData {
    static IBlockModelData empty() {
        return ClientServices.MODELS.empty();
    }

    /**
     * Check if this data has a property, even if the value is {@code null}. Can be
     * used by code that intends to fill in data for a render pipeline.
     *
     * @param prop The property to check for inclusion in this model data
     * @return {@code true} if this data has the given property, even if no value is present
     */
    boolean hasProperty(IModelDataKey<?> prop);

    @Nullable <T> T getData(IModelDataKey<T> prop);
}
