package com.grim3212.assorted.lib.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigGroup {
    public final String groupName;
    private final List<ConfigOption<?>> options;

    public ConfigGroup(String groupName) {
        this.groupName = groupName;
        this.options = new ArrayList<>();
    }

    public List<ConfigOption<?>> getOptions() {
        return options;
    }

    public ConfigGroup addOptions(ConfigOption<?>... options) {
        this.options.addAll(Arrays.asList(options));
        return this;
    }
}
