package com.grim3212.assorted.lib.test.config;

import com.grim3212.assorted.lib.config.ConfigBuilder;
import com.grim3212.assorted.lib.config.ConfigGroup;
import com.grim3212.assorted.lib.config.ConfigOption;
import com.grim3212.assorted.lib.config.ConfigType;

public class LibTestConfig {

    public static final ConfigOption.ConfigOptionInteger testMinMax = new ConfigOption.ConfigOptionInteger("testMinMax", 1, "Cool Comment", 0, 100);
    public static final ConfigOption<Boolean> testBool = new ConfigOption<>("testBool", ConfigOption.OptionType.BOOLEAN, false, "Cool boolean");

    private static final ConfigGroup subGroupTest = new ConfigGroup("ParentGroup");
    private static final ConfigGroup childSubGroup = new ConfigGroup("ChildGroup");
    public static final ConfigOption<Boolean> childConfig = new ConfigOption<>("childConfig", ConfigOption.OptionType.BOOLEAN, false, "Child config option");


    public static final ConfigBuilder COMMON = new ConfigBuilder(ConfigType.COMMON)
            .addGroups(new ConfigGroup("TESTING").addOptions(testMinMax, testBool))
            .addGroups(subGroupTest.addSubGroups(childSubGroup.addOptions(childConfig)));

}
