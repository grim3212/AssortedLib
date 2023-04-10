package com.grim3212.assorted.lib.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.concurrent.CompletableFuture;

public class FabricWorldGenProvider extends FabricDynamicRegistryProvider {

    private final LibWorldGenProvider commonWorldGen;
    private final String modId;

    public FabricWorldGenProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, String modId, LibWorldGenProvider commonWorldGen) {
        super(output, registriesFuture);
        this.commonWorldGen = commonWorldGen;
        this.modId = modId;
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        for (ResourceKey<? extends Registry<?>> registry : this.commonWorldGen.registries()) {
            entries.addAll(registries.lookupOrThrow(registry));
        }
    }

    @Override
    public String getName() {
        return this.modId + ", world gen provider";
    }
}
