package com.grim3212.assorted.lib.client.model;

import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.model.ItemOverrides;

public class ItemOverridesExtension extends ItemOverrides {
    protected final IModelBakingContext context;

    protected ItemOverridesExtension(IModelBakingContext context) {
        this.context = context;
    }
}
