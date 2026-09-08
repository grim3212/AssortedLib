package com.grim3212.assorted.lib.client.model.data;

import net.neoforged.neoforge.model.data.ModelProperty;

public class ForgeModelPropertyPlatformDelegate<T> implements IModelDataKey<T> {

    private final ModelProperty<T> property;

    public ForgeModelPropertyPlatformDelegate(final ModelProperty<T> property) {
        this.property = property;
    }

    public ModelProperty<T> getProperty() {
        return property;
    }
}
