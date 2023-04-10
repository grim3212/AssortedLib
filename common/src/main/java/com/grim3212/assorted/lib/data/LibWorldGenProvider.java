package com.grim3212.assorted.lib.data;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public abstract class LibWorldGenProvider {
    public abstract void addToWorldGem(RegistrySetBuilder builder);

    public abstract List<ResourceKey<? extends Registry<?>>> registries();
}
