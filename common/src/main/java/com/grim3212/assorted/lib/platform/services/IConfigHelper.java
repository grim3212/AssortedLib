package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;

public interface IConfigHelper {

    /**
     * Creates a new configuration builder, which will build a configuration of the given type, and name the configuration with the given name.
     *
     * @param type The type of the configuration.
     * @param name The name of the configuration.
     * @return The builder for the configuration.
     */
    IConfigurationBuilder createBuilder(final ConfigurationType type, final String name);
}
