package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ForgeWorldGenProvider {

    private final LibWorldGenProvider commonWorldGen;
    private final String modId;

    public ForgeWorldGenProvider(String modId, LibWorldGenProvider commonWorldGen) {
        this.commonWorldGen = commonWorldGen;
        this.modId = modId;
    }

    public DatapackBuiltinEntriesProvider datpackEntriesProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        RegistrySetBuilder coreBuilder = new RegistrySetBuilder();
        this.commonWorldGen.addToWorldGem(coreBuilder);
        return new DatapackBuiltinEntriesProvider(output, registries, coreBuilder, Set.of(this.modId));
    }

}
