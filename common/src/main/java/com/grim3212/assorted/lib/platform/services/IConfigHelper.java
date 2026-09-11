package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;

public interface IConfigHelper {

    /** Creates a builder for a configuration of the given type and name. */
    IConfigurationBuilder createBuilder(final ConfigurationType type, final String name);
}
