package com.grim3212.assorted.lib.proxy;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.FabricFluidManager;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;

public class LibFabricClientProxy implements LibFabricProxy {

    @Override
    public Component getFluidDisplayName(final Fluid fluid) {
        return FluidVariantAttributes.getName(FabricFluidManager.makeVariant(new FluidInformation(fluid)));
    }

    @Override
    public int getFluidColor(final FluidInformation fluid) {
        return FluidVariantRendering.getColor(FabricFluidManager.makeVariant(fluid));
    }
}
