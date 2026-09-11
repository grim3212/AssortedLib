package com.grim3212.assorted.lib.client.model.loaders.context;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * The context in which the model is baked
 */
public interface IModelBakingContext {

    /**
     * The unbaked model named {@code unbakedModel}, or null if not found; can be used to get a
     * parent. The game tracks requested models and throws on a circular dependency.
     */
    UnbakedModel getUnbakedModel(final Identifier unbakedModel);

    /** A material from a supported super model or the context, or empty if not found. */
    Optional<Material> getMaterial(final String name);

    /**
     * Indicates if this context is baking a model that is rendered using 3D Light.
     *
     * @return {@code true} if the model is rendered using 3D Light, {@code false} otherwise.
     */
    boolean isGui3d();

    /**
     * Indicates if this context is baking a model that is rendered using a blocks lighting layout.
     *
     * @return {@code true} if the model is rendered using a blocks lighting layout, {@code false} otherwise.
     */
    boolean useBlockLight();

    /**
     * Indicates if this context is using ambient occlusion.
     *
     * @return {@code true} if ambient occlusion is used, {@code false} otherwise.
     */
    boolean useAmbientOcclusion();

    /**
     * The item transforms which should be applied to the model.
     *
     * @return The item transforms.
     */
    ItemTransforms getTransforms();

    // No getItemOverrides any more: item variants are picked by data driven ItemModel types
    // declared in the item's model json, not assembled by a model loader while baking.
}
