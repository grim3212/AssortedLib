package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface IRegistryFactory {

    <T> RegistryProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey, String modId);

    default <T> RegistryProvider<T> create(Registry<T> registry, String modId) {
        return create(registry.key(), modId);
    }
}
