package com.grim3212.assorted.lib.client.model.baked;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.world.item.ItemStack;

/**
 * Defines an item model which is compounded of multiple models and can be queried for the models it
 * is composed of.
 */
// TODO(26.2): not expressible on the item pipeline; kept as a marker so the loader modules compile.
//  It answered "which models do you draw for this stack" so callers could re-render the layers.
//  Item rendering is push-only now (ItemModel#update fills an ItemStackRenderState and returns
//  nothing); a caller that wants the layers must run update() into its own render state.
public interface ICompoundItemBakedModel extends ItemModel {

    /** Whether this model draws anything at all for {@code stack}. */
    default boolean hasLayers(final ItemStack stack) {
        return true;
    }
}
