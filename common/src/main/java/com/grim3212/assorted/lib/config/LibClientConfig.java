package com.grim3212.assorted.lib.config;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class LibClientConfig {

    public final Supplier<Boolean> showManualPageIndicator;

    public LibClientConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.CLIENT_ONLY, LibConstants.MOD_ID + "-client");

        showManualPageIndicator = builder.defineBoolean("manual.showPageIndicator", true,
                "Show a check mark beside the crosshair when what it is on has an instruction manual page.");

        builder.setup();
    }
}
