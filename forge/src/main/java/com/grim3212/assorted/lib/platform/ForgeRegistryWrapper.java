package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ForgeRegistryWrapper<T> implements ILoaderRegistry<T> {

    private final ForgeRegistry<T> forgeRegistry;

    private ForgeRegistryWrapper(ForgeRegistry<T> forgeRegistry) {
        this.forgeRegistry = forgeRegistry;
    }

    public static <T, V> ILoaderRegistry<T> getRegistry(ResourceKey<? extends Registry<T>> key) {
        ForgeRegistry<V> registry = RegistryManager.ACTIVE.getRegistry(key.location());
        ILoaderRegistry<V> registryWrapper = new ForgeRegistryWrapper<>(registry);
        @SuppressWarnings("unchecked")
        ILoaderRegistry<T> castRegistry = (ILoaderRegistry<T>) registryWrapper;
        return castRegistry;
    }

    @Override
    public Stream<T> getValues() {
        return StreamSupport.stream(this.forgeRegistry.spliterator(), false);
    }

    @Override
    public Optional<T> getValue(ResourceLocation resourceLocation) {
        T value = this.forgeRegistry.getValue(resourceLocation);
        return Optional.ofNullable(value);
    }

    @Override
    public boolean contains(T entry) {
        return this.forgeRegistry.containsValue(entry);
    }

    @Override
    public ResourceLocation getRegistryName(T entry) {
        return this.forgeRegistry.getResourceKey(entry).map(ResourceKey::location).orElse(this.forgeRegistry.getDefaultKey());
    }
}
