package com.grim3212.assorted.lib.registry;

import net.minecraft.resources.Identifier;

import java.util.Optional;
import java.util.stream.Stream;

public interface ILoaderRegistry<T> {
    Stream<T> getValues();

    Optional<T> getValue(Identifier resourceLocation);

    boolean contains(T entry);

    boolean containsKey(Identifier resourceLocation);

    Identifier getRegistryName(T entry);
}
