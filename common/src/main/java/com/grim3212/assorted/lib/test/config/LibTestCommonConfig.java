package com.grim3212.assorted.lib.test.config;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class LibTestCommonConfig {

    public final Supplier<String> testString;

    public LibTestCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, LibConstants.MOD_ID + "-common");

        testString = builder.defineString("common.test_string", "test", "Test string comment");

        builder.setup();
    }

}
