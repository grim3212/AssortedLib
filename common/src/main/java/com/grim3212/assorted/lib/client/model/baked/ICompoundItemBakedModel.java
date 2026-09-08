package com.grim3212.assorted.lib.client.model.baked;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.world.item.ItemStack;

/**
 * Defines an item model which is compounded of multiple models and can be queried for the models it
 * is composed of.
 */
// TODO(26.2): not expressible on the new item pipeline, left as a marker so the loader modules keep
//  compiling and the intent is not lost.
//  What it used to do: an ICompoundItemBakedModel was a BakedModel that could be asked, for a given
//  ItemStack, "which BakedModels do you actually draw?" - getLayers(stack, fabulous) returned that
//  list so callers (JEI ghost rendering, the loader item renderers) could re-render the individual
//  layers themselves.
//  Why it cannot be expressed: item rendering is now push-only. An ItemModel does not return
//  anything; ItemModel#update(ItemStackRenderState, ItemStack, ItemModelResolver, ItemDisplayContext,
//  ClientLevel, ItemOwner, int) mutates an ItemStackRenderState, appending layers via
//  ItemStackRenderState#newLayer(). CompositeModel simply forwards update() to each child. There is
//  no accessor that hands back the child ItemModels, and LayerRenderState is a write-only sink
//  (setUsesBlockLight/setParticleMaterial/setItemTransform/prepareQuadList), so the layers only exist
//  for the duration of one update() call. The nearest replacement for a caller that wants the layers
//  is to run update() against its own ItemStackRenderState and read that - which is a different
//  shape entirely and belongs on the call site, not here.
public interface ICompoundItemBakedModel extends ItemModel {

    /**
     * The stack this compound model was resolved for.
     *
     * @param stack The stack to check.
     * @return {@code true} if this model draws anything at all for the stack.
     */
    default boolean hasLayers(final ItemStack stack) {
        return true;
    }
}
