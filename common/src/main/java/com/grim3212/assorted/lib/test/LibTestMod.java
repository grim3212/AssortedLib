package com.grim3212.assorted.lib.test;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.test.config.LibTestClientConfig;

public class LibTestMod {

    public static LibTestClientConfig clientConfig;

    public static void init() {
        clientConfig = new LibTestClientConfig();


    }

    public static void getConfig() {
        LibConstants.LOG.info("Test float list config {}", clientConfig.testFloatList.get());
        LibConstants.LOG.info("Test boolean config {}", clientConfig.testBoolean.get());
        LibConstants.LOG.info("Test int config {}", clientConfig.testInteger.get());
        LibConstants.LOG.info("Test string config {}", clientConfig.testString.get());
    }
}
