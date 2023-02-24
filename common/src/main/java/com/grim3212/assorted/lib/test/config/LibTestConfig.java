package com.grim3212.assorted.lib.test.config;

import com.grim3212.assorted.lib.config.ConfigBuilder;
import com.grim3212.assorted.lib.config.ConfigGroup;
import com.grim3212.assorted.lib.config.ConfigOption;
import com.grim3212.assorted.lib.config.ConfigType;

public class LibTestConfig {

    public static final ConfigOption.ConfigOptionInteger testMinMax = new ConfigOption.ConfigOptionInteger("testMinMax", 1, "Cool Comment", 0, 100);
    public static final ConfigOption<Boolean> testBool = new ConfigOption<>("testBool", ConfigOption.OptionType.BOOLEAN, false, "Cool boolean");

    public static final ConfigBuilder COMMON = new ConfigBuilder(ConfigType.COMMON)
            .addGroups(new ConfigGroup("TESTING").addOptions(testMinMax, testBool));

}
