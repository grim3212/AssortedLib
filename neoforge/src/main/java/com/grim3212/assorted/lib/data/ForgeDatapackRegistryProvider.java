package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.platform.ForgeConditionHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ForgeDatapackRegistryProvider {

    private final LibDatapackRegistryProvider commonEntries;
    private final String modId;

    public ForgeDatapackRegistryProvider(String modId, LibDatapackRegistryProvider commonEntries) {
        this.commonEntries = commonEntries;
        this.modId = modId;
    }

    public DatapackBuiltinEntriesProvider datpackEntriesProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        RegistrySetBuilder coreBuilder = new RegistrySetBuilder();
        this.commonEntries.addEntries(coreBuilder);

        Map<ResourceKey<?>, List<ICondition>> conditions = new HashMap<>();
        this.commonEntries.conditions().forEach((key, entryConditions) -> conditions.put(key, entryConditions.stream().map(ForgeConditionHelper::unwrap).toList()));

        // Entries carry load conditions, so they are written for both loaders like recipes are.
        return new DatapackBuiltinEntriesProvider(output, registries, coreBuilder, conditions, Set.of(this.modId)) {
            @Override
            public CompletableFuture<?> run(CachedOutput cachedOutput) {
                return super.run(CrossLoaderData.wrap(cachedOutput));
            }
        };
    }

}
