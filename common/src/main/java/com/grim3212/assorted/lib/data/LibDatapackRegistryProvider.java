package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.core.conditions.LibConditionProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Map;

/**
 * Entries for any datapack registry - worldgen features and biomes, enchantments, and so on - built
 * from a {@link RegistrySetBuilder} and written by each loader's datapack registry datagen.
 */
public abstract class LibDatapackRegistryProvider {
    public abstract void addEntries(RegistrySetBuilder builder);

    public abstract List<ResourceKey<? extends Registry<?>>> registries();

    /**
     * Load conditions per entry. An entry whose conditions fail is not registered at all when the
     * datapack loads, so anything that names it - a tag, another entry's holder set - has to do so
     * optionally, or it fails along with it.
     */
    public Map<ResourceKey<?>, List<LibConditionProvider>> conditions() {
        return Map.of();
    }
}
