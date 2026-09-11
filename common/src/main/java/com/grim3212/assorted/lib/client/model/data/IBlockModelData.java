package com.grim3212.assorted.lib.client.model.data;

import com.grim3212.assorted.lib.platform.ClientServices;
import org.jetbrains.annotations.Nullable;

public interface IBlockModelData {
    static IBlockModelData empty() {
        return ClientServices.MODELS.empty();
    }

    /**
     * Whether this data has {@code prop}, even with a {@code null} value. Useful for code that
     * fills in data for a render pipeline.
     */
    boolean hasProperty(IModelDataKey<?> prop);

    @Nullable <T> T getData(IModelDataKey<T> prop);
}
