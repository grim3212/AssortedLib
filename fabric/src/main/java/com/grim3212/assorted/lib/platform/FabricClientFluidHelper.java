package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.services.IClientFluidHelper;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

public class FabricClientFluidHelper implements IClientFluidHelper {

    @Override
    public int getFluidColor(FluidInformation fluid) {
        return FluidVariantRendering.getColor(FabricFluidManager.makeVariant(fluid));
    }

    @Override
    public TextureAtlasSprite getSprite(FluidInformation fluid) {
        return FluidVariantRendering.getSprite(FabricFluidManager.makeVariant(fluid));
    }

    @Override
    public Identifier getFlowingFluidTexture(final FluidInformation fluidInformation) {
        return FluidVariantRendering.getSprite(FabricFluidManager.makeVariant(fluidInformation)).contents().name();
    }

    @Override
    public Identifier getFlowingFluidTexture(final Fluid fluid) {
        return getFlowingFluidTexture(new FluidInformation(fluid));
    }

    @Override
    public Identifier getStillFluidTexture(final FluidInformation fluidInformation) {
        return FluidVariantRendering.getSprite(FabricFluidManager.makeVariant(fluidInformation)).contents().name();
    }

    @Override
    public Identifier getStillFluidTexture(final Fluid fluid) {
        return getFlowingFluidTexture(new FluidInformation(fluid));
    }
}
