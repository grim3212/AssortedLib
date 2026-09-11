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
 * TODO(26.2): a fluid's textures are a baked {@link FluidModel} per {@link Fluid}, readable only
 *  once models have baked, so everything resolves from the fluid alone; the extra data a
 *  {@link FluidInformation} carries only still feeds the tint.
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
