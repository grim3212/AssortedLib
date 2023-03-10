package com.grim3212.assorted.lib.platform;

import com.google.common.collect.Sets;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.ForgeDelegateConfigurationBuilder;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.services.IConfigHelper;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Set;

public class ForgeConfigHelper implements IConfigHelper {
    private final Set<String> availableKeys = Sets.newConcurrentHashSet();

    @Override
    public IConfigurationBuilder createBuilder(final ConfigurationType type, final String name) {
        return new ForgeDelegateConfigurationBuilder(forgeConfigSpec -> {
            final ModConfig config = new ModConfig(remapType(type), forgeConfigSpec, ModLoadingContext.get().getActiveContainer(), String.format("%s.toml", name));
            ModLoadingContext.get().getActiveContainer().addConfig(config);
        }, availableKeys::add);
    }

    private static ModConfig.Type remapType(final ConfigurationType type) {
        return switch (type) {
            case CLIENT_ONLY -> ModConfig.Type.CLIENT;
            case NOT_SYNCED -> ModConfig.Type.COMMON;
            case SYNCED -> ModConfig.Type.SERVER;
        };
    }

    public Set<String> getAvailableKeys() {
        return availableKeys;
    }
}
