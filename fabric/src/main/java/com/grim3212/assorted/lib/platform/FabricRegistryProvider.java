package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IRegistryFactory;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FabricRegistryProvider implements IRegistryFactory {

    @Override
    public <T> RegistryProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey, String modId) {
        return new Provider<>(modId, resourceKey);
    }

    @Override
    public <T> RegistryProvider<T> create(Registry<T> registry, String modId) {
        return new Provider<>(modId, registry);
    }

    private static class Provider<T> implements RegistryProvider<T> {
        private final String modId;
        private final Registry<T> registry;

        private final Set<IRegistryObject<T>> entries = new HashSet<>();
        private final Set<IRegistryObject<T>> entriesView = Collections.unmodifiableSet(entries);

        @SuppressWarnings({"unchecked"})
        private Provider(String modId, ResourceKey<? extends Registry<T>> key) {
            this.modId = modId;

            final var reg = BuiltInRegistries.REGISTRY.get(key.location());
            if (reg == null) {
                throw new RuntimeException("Registry with name " + key.location() + " was not found!");
            }
            registry = (Registry<T>) reg;
        }

        private Provider(String modId, Registry<T> registry) {
            this.modId = modId;
            this.registry = registry;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <I extends T> IRegistryObject<I> register(String name, Supplier<? extends I> supplier) {
            final var rl = new ResourceLocation(modId, name);
            final var obj = Registry.register(registry, rl, supplier.get());
            final var ro = new IRegistryObject<I>() {
                final ResourceKey<I> key =
                        ResourceKey.create((ResourceKey<? extends Registry<I>>) registry.key(), rl);

                @Override
                public ResourceKey<I> getResourceKey() {
                    return key;
                }

                @Override
                public ResourceLocation getId() {
                    return rl;
                }

                @Override
                public I get() {
                    return obj;
                }

                @Override
                public Holder<I> asHolder() {
                    return (Holder<I>) registry.getHolderOrThrow((ResourceKey<T>) this.key);
                }
            };
            entries.add((IRegistryObject<T>) ro);
            return ro;
        }

        @Override
        public Collection<IRegistryObject<T>> getEntries() {
            return entriesView;
        }


        @Override
        public Stream<T> getValues() {
            return registry.stream();
        }

        @Override
        public Optional<T> getValue(ResourceLocation resourceLocation) {
            return entriesView.stream().filter(x -> x.getResourceKey().equals(resourceLocation)).map(x -> x.get()).findFirst();
        }

        @Override
        public boolean contains(T entry) {
            return this.getValues().anyMatch(x -> entry.equals(x));
        }

        @Override
        public ResourceLocation getRegistryName(T entry) {
            return this.registry.getKey(entry);
        }
    }
}
