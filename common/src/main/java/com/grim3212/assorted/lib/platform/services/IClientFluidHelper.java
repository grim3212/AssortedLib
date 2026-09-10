package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

/**
 * <b>Every method here is render time only.</b> A fluid's appearance is a baked
 * {@code net.minecraft.client.renderer.block.FluidModel}, and {@code ModelManager} bakes those
 * <em>after</em> every other model and only publishes them in {@code apply}, so calling any of this
 * during model baking throws {@code NullPointerException: Fluid models not yet initialized}. A model
 * that needs a fluid's sprite therefore cannot be a model json loader - see AssortedTools'
 * {@code FluidContainerItemModel} and section 3g of the upgrade guide.
 */
public interface IClientFluidHelper {

    int getFluidColor(final FluidInformation fluid);

    TextureAtlasSprite getSprite(final FluidInformation fluid);

    /**
     * Gains access to the texture that is used to render a flowing fluid.
     *
     * @param fluidInformation The fluid to get the texture for.
     * @return The texture.
     */
    Identifier getFlowingFluidTexture(final FluidInformation fluidInformation);

    /**
     * Gains access to the texture that is used to render a flowing fluid.
     *
     * @param fluid The fluid to get the texture for.
     * @return The texture.
     */
    Identifier getFlowingFluidTexture(final Fluid fluid);

    /**
     * Gains access to the texture that is used to render a still fluid.
     *
     * @param fluidInformation The fluid to get the texture for.
     * @return The texture.
     */
    Identifier getStillFluidTexture(final FluidInformation fluidInformation);

    /**
     * Gains access to the texture that is used to render a still fluid.
     *
     * @param fluid The fluid to get the texture for.
     * @return The texture.
     */
    Identifier getStillFluidTexture(final Fluid fluid);
}
