package com.grim3212.assorted.lib.client.model.baked;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Defines an item model which is aware of transform data and can supply it per display context.
 */
public interface ITransformAwareBakedModel extends ItemModel {

    /**
     * The transform to apply for the given display context.
     *
     * @param cameraTransformType The camera transform to look up.
     * @return The transform to apply, {@link ItemTransform#NO_TRANSFORM} for none.
     */
    ItemTransform getTransform(ItemDisplayContext cameraTransformType);

    // TODO(26.2): the old handlePerspective(ItemDisplayContext, PoseStack) callback is gone.
    //  What it used to do: the item renderer asked the model to mutate the PoseStack itself right
    //  before drawing, which let a model apply an arbitrary, per-frame transformation (and even swap
    //  itself out for another model) instead of the fixed data from its ItemTransforms.
    //  Why it cannot be expressed: 26.2 is retained mode. ItemModel#update pushes a plain
    //  ItemTransform record into ItemStackRenderState.LayerRenderState#setItemTransform (plus an
    //  optional Matrix4fc via setLocalTransform), and the engine applies it later while flushing the
    //  submit nodes; the model is not on the stack at draw time and never sees a PoseStack. Both
    //  ItemTransform and ItemTransforms are records now, so the old trick of subclassing them to
    //  intercept getTransform/apply is impossible as well. The remaining honest shape is the one
    //  above: hand back a real ItemTransform, computed however you like, at update() time.
}
