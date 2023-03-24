package com.grim3212.assorted.lib.client.model;

import net.minecraft.client.resources.model.ModelBakery;

/**
 * Fabric specific platform interface which gives access to the model bakery.
 */
public interface IModelBakeryAccessor {

    /**
     * The model bakery.
     *
     * @return The model bakery.
     */
    ModelBakery getModelBakery();
}
