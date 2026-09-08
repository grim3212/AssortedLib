package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * NeoForge no longer keeps a parallel registry system: {@code ForgeRegistry} and
 * {@code RegistryManager.ACTIVE} are gone and everything, modded registries included, lives in the
 * vanilla registry of registries. This is therefore a thin wrapper over a plain {@link Registry},
 * exactly like the Fabric side.
 */
public class ForgeRegistryWrapper<T> implements ILoaderRegistry<T> {

    private final Registry<T> registry;

    private ForgeRegistryWrapper(Registry<T> registry) {
        this.registry = registry;
    }

    public static <T> ILoaderRegistry<T> getRegistry(ResourceKey<? extends Registry<T>> key) {
        Registry<? extends Registry<?>> rootRegistry = BuiltInRegistries.REGISTRY;
        Registry<?> registry = rootRegistry.getValue(key.identifier());
        if (registry == null) {
            throw new NullPointerException("Could not find registry for key: " + key);
        }

        ILoaderRegistry<?> registryWrapper = new ForgeRegistryWrapper<>(registry);
        @SuppressWarnings("unchecked")
        ILoaderRegistry<T> castRegistry = (ILoaderRegistry<T>) registryWrapper;
        return castRegistry;
    }

    @Override
    public Stream<T> getValues() {
        return this.registry.stream();
    }

    @Override
    public Optional<T> getValue(Identifier resourceLocation) {
        return Optional.ofNullable(this.registry.getValue(resourceLocation));
    }

    @Override
    public boolean containsKey(Identifier resourceLocation) {
        return this.registry.containsKey(resourceLocation);
    }

    @Override
    public boolean contains(T entry) {
        return this.registry.getKey(entry) != null;
    }

    @Override
    public Identifier getRegistryName(T entry) {
        return this.registry.getKey(entry);
    }
}
