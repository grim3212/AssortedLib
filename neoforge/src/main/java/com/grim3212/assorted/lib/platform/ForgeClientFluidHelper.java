package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.services.IClientFluidHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.fluid.FluidTintSource;

/**
 * TODO(26.2): {@code IClientFluidTypeExtensions#getStillTexture}, {@code #getFlowingTexture} and
 *  {@code #getTintColor} are gone. A fluid's appearance is a baked {@link FluidModel} registered per
 *  {@link Fluid} through {@code RegisterFluidModelsEvent}, so it can only be read off the client
 *  model manager and only once models have been baked. Everything here therefore resolves through
 *  the fluid alone; the extra fluid data a {@link FluidInformation} carries can no longer pick a
 *  different texture the way a FluidStack aware extension could - it only still feeds the tint.
 */
public class ForgeClientFluidHelper implements IClientFluidHelper {

    @Override
    public int getFluidColor(FluidInformation fluid) {
        final FluidTintSource tintSource = fluidModel(fluid.fluid()).fluidTintSource();
        return tintSource == null ? -1 : tintSource.colorAsStack(ForgeFluidManager.buildFluidStack(fluid));
    }

    @Override
    public TextureAtlasSprite getSprite(FluidInformation fluid) {
        return fluidModel(fluid.fluid()).stillMaterial().sprite();
    }

    @Override
    public Identifier getFlowingFluidTexture(final FluidInformation fluidInformation) {
        return getFlowingFluidTexture(fluidInformation.fluid());
    }

    @Override
    public Identifier getFlowingFluidTexture(final Fluid fluid) {
        return fluidModel(fluid).flowingMaterial().sprite().contents().name();
    }

    @Override
    public Identifier getStillFluidTexture(final FluidInformation fluidInformation) {
        return getStillFluidTexture(fluidInformation.fluid());
    }

    @Override
    public Identifier getStillFluidTexture(final Fluid fluid) {
        return fluidModel(fluid).stillMaterial().sprite().contents().name();
    }

    private static FluidModel fluidModel(final Fluid fluid) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState());
    }
}
