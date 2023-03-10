package com.grim3212.assorted.lib.proxy;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.Services;

public interface LibForgeProxy {
    LibForgeProxy INSTANCE = make();

    private static LibForgeProxy make() {
        if (Services.PLATFORM.isPhysicalClient()) {
            return new LibForgeClientProxy();
        } else {
            return new LibForgeProxy() {
            };
        }
    }

    default int getFluidColor(final FluidInformation fluid) {
        return 0xffffff;
    }

    default void starting() {
    }

}
