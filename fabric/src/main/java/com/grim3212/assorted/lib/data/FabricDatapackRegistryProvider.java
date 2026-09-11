package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.core.conditions.LibConditionProvider;
import com.grim3212.assorted.lib.platform.FabricConditionHelper;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FabricDatapackRegistryProvider extends FabricDynamicRegistryProvider {

    private final LibDatapackRegistryProvider commonEntries;
    private final String modId;

    public FabricDatapackRegistryProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, String modId, LibDatapackRegistryProvider commonEntries) {
        super(output, registriesFuture);
        this.commonEntries = commonEntries;
        this.modId = modId;
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        Map<ResourceKey<?>, List<LibConditionProvider>> conditions = this.commonEntries.conditions();
        for (ResourceKey<? extends Registry<?>> registry : this.commonEntries.registries()) {
            addAll(registries.lookupOrThrow(registry), entries, conditions);
        }
    }

    /**
     * The same selection {@code Entries#addAll} makes - this mod's namespace - added one entry at a
     * time so each can carry its own conditions.
     */
    private <T> void addAll(HolderLookup.RegistryLookup<T> lookup, Entries entries, Map<ResourceKey<?>, List<LibConditionProvider>> conditions) {
        lookup.listElementIds().filter(key -> key.identifier().getNamespace().equals(this.modId)).forEach(key -> {
            ResourceCondition[] entryConditions = conditions.getOrDefault(key, List.of()).stream().map(FabricConditionHelper::unwrap).toArray(ResourceCondition[]::new);
            entries.add(lookup, key, entryConditions);
        });
    }

    @Override
    public String getName() {
        return this.modId + ", datapack registry entries";
    }
}
