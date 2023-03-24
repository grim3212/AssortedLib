package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public interface IClientFluidHelper {

    int getFluidColor(final FluidInformation fluid);

    TextureAtlasSprite getSprite(final FluidInformation fluid);

    /**
     * Gains access to the texture that is used to render a flowing fluid.
     *
     * @param fluidInformation The fluid to get the texture for.
     * @return The texture.
     */
    ResourceLocation getFlowingFluidTexture(final FluidInformation fluidInformation);

    /**
     * Gains access to the texture that is used to render a flowing fluid.
     *
     * @param fluid The fluid to get the texture for.
     * @return The texture.
     */
    ResourceLocation getFlowingFluidTexture(final Fluid fluid);

    /**
     * Gains access to the texture that is used to render a still fluid.
     *
     * @param fluidInformation The fluid to get the texture for.
     * @return The texture.
     */
    ResourceLocation getStillFluidTexture(final FluidInformation fluidInformation);

    /**
     * Gains access to the texture that is used to render a still fluid.
     *
     * @param fluid The fluid to get the texture for.
     * @return The texture.
     */
    ResourceLocation getStillFluidTexture(final Fluid fluid);
}
