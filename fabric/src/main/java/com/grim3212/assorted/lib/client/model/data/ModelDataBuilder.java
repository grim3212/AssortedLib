package com.grim3212.assorted.lib.client.model.data;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ModelDataBuilder implements IModelDataBuilder {
    private final Map<IModelDataKey<?>, Object> values = Maps.newHashMap();

    @Override
    public IBlockModelData build() {
        return new IBlockModelData() {

            private final Map<IModelDataKey<?>, Object> data = Maps.newHashMap(values);

            @Override
            public boolean hasProperty(final IModelDataKey<?> prop) {
                return data.containsKey(prop);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> @Nullable T getData(final IModelDataKey<T> prop) {
                return (T) data.get(prop);
            }
        };
    }

    @Override
    public <T> IModelDataBuilder withInitial(final IModelDataKey<T> key, final T value) {
        this.values.put(key, value);
        return this;
    }


}
