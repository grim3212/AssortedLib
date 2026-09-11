package com.grim3212.assorted.lib.client.model.baked;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Defines an item model which is aware of transform data and can supply it per display context.
 */
public interface ITransformAwareBakedModel extends ItemModel {

    /**
     * The transform for {@code cameraTransformType}, or {@link ItemTransform#NO_TRANSFORM} for
     * none.
     */
    ItemTransform getTransform(ItemDisplayContext cameraTransformType);

    // TODO(26.2): handlePerspective(ItemDisplayContext, PoseStack) is gone: rendering is retained
    //  mode, the model never sees the PoseStack, and ItemTransform(s) are records that cannot be
    //  subclassed. Return a real ItemTransform from getTransform, computed at update() time.
}
