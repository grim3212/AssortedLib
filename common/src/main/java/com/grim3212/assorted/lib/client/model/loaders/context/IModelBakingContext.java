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
     * Retrieves an unbaked model from the context.
     * This can be used to get a parent context.
     * <p>
     * Note the game keeps track of all unbaked models that are requested to be loaded, and will throw
     * an exception if a circular dependency is detected.
     *
     * @param unbakedModel The name of the unbaked model to load.
     * @return The unbaked model, or null if it could not be found.
     */
    UnbakedModel getUnbakedModel(final Identifier unbakedModel);

    /**
     * Retries a material from a supported super model or context.
     *
     * @param name The name of the material.
     * @return The material, or empty if it could not be found.
     */
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

    // TODO(26.2): getItemOverrides(ModelBaker) was removed along with ItemOverrides itself. A baking
    //  context has nothing left to hand back: item variants are picked by data driven ItemModel types
    //  (SelectItemModel / ConditionalItemModel / RangeSelectItemModel and the codec registered
    //  properties under client.renderer.item.properties.**) declared in the item's own model json,
    //  not by a list of overrides a model loader could assemble while baking.
}
