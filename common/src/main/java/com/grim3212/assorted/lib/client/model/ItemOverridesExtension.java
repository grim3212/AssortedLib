package com.grim3212.assorted.lib.client.model;

import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;

/**
 * Carries the baking context a custom model loader used to need in order to build its item overrides.
 */
// TODO(26.2): ItemOverrides no longer exists, so there is nothing to extend: per-stack model choice
//  is a codec-registered ItemModel.Unbaked selected by the item's json, chosen before baking rather
//  than per draw. Kept only until the loader modules' context plumbing is removed.
public class ItemOverridesExtension {
    protected final IModelBakingContext context;

    protected ItemOverridesExtension(IModelBakingContext context) {
        this.context = context;
    }
}
