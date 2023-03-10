package com.grim3212.assorted.lib.test.config;

import com.google.common.collect.Lists;
import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.List;
import java.util.function.Supplier;

public class LibTestClientConfig {

    public final Supplier<List<? extends Float>> testFloatList;
    public final Supplier<Boolean> testBoolean;
    public final Supplier<Integer> testInteger;
    public final Supplier<String> testString;

    public LibTestClientConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.CLIENT_ONLY, LibConstants.MOD_ID + "-client");

        testBoolean = builder.defineBoolean("config.test_boolean", false);
        testInteger = builder.defineInteger("config.test_integer", 11, 10, 1000);
        testString = builder.defineString("config.test_string", "test");
        testFloatList = builder.defineList("config.test_float_list", Lists.newArrayList(0.85f, 0.0f, 0.0f, 0.65f), Float.class);

        builder.setup();
    }

}
