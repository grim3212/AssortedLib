package com.grim3212.assorted.lib.proxy;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;

public interface LibFabricProxy {

    LibFabricProxy INSTANCE = make();

    private static LibFabricProxy make() {
        if (Services.PLATFORM.isPhysicalClient()) {
            return new LibFabricClientProxy();
        } else {
            return new LibFabricProxy() {
            };
        }
    }

    default Component getFluidDisplayName(final Fluid fluid) {
        return fluid.defaultFluidState().createLegacyBlock().getBlock().getName();
    }

    default int getFluidColor(final FluidInformation fluid) {
        return 0xffffff;
    }
}
