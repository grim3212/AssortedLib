package com.grim3212.assorted.lib.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigBuilder {
    private final ConfigType type;
    private final List<ConfigGroup> groups;

    public ConfigBuilder(ConfigType type) {
        this.type = type;
        this.groups = new ArrayList<>();
    }

    public ConfigType getType() {
        return type;
    }

    public List<ConfigGroup> getGroups() {
        return groups;
    }

    public ConfigBuilder addGroups(ConfigGroup... groups) {
        this.groups.addAll(Arrays.asList(groups));
        return this;
    }
}
