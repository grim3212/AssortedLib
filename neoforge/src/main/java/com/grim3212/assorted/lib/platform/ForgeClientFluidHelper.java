package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.services.IClientFluidHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public class ForgeClientFluidHelper implements IClientFluidHelper {

    @Override
    public int getFluidColor(FluidInformation fluid) {
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid.fluid());
        return clientFluid.getTintColor();
    }

    @Override
    public TextureAtlasSprite getSprite(FluidInformation fluid) {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluid.fluid()).getStillTexture(ForgeFluidManager.buildFluidStack(fluid)));
    }

    @Override
    public Identifier getFlowingFluidTexture(final FluidInformation fluidInformation) {
        return IClientFluidTypeExtensions.of(fluidInformation.fluid())
                .getFlowingTexture(ForgeFluidManager.buildFluidStack(fluidInformation));
    }

    @Override
    public Identifier getFlowingFluidTexture(final Fluid fluid) {
        return IClientFluidTypeExtensions.of(fluid).getFlowingTexture();
    }

    @Override
    public Identifier getStillFluidTexture(final FluidInformation fluidInformation) {
        return IClientFluidTypeExtensions.of(fluidInformation.fluid()).getStillTexture(ForgeFluidManager.buildFluidStack(fluidInformation));
    }

    @Override
    public Identifier getStillFluidTexture(final Fluid fluid) {
        return IClientFluidTypeExtensions.of(fluid).getStillTexture();
    }
}
