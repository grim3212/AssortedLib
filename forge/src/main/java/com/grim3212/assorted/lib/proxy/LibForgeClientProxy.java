package com.grim3212.assorted.lib.proxy;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.test.LibTestClientMod;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class LibForgeClientProxy implements LibForgeProxy {


    @Override
    public int getFluidColor(FluidInformation fluid) {
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid.fluid());
        return clientFluid.getTintColor();
    }

    @Override
    @SuppressWarnings("removal")
    public void starting() {
        if (!Services.PLATFORM.isProduction()) {
            LibTestClientMod.init();
        }
    }
}
