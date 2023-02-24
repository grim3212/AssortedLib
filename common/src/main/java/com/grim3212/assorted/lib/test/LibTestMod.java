package com.grim3212.assorted.lib.test;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.test.config.LibTestConfig;

public class LibTestMod {
    public static void init() {
        Services.PLATFORM.setupCommonConfig(LibConstants.MOD_ID, LibTestConfig.COMMON);

        LibConstants.LOG.info("This minMax value is {}", LibTestConfig.testMinMax.getValue());
        LibConstants.LOG.info("This bool value is {}", LibTestConfig.testBool.getValue());
        LibConstants.LOG.info("Get value as int {}", (Integer) LibTestConfig.testMinMax.getValue());
    }
}
