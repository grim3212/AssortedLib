package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;

import java.util.concurrent.CompletableFuture;

public class FabricBiomeTagProvider extends BiomeTagsProvider {

    private final LibBiomeTagProvider commonBiomes;

    public FabricBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, LibBiomeTagProvider commonBiomes) {
        super(output, lookup);
        this.commonBiomes = commonBiomes;
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonBiomes.addCommonTags(this::tag);
    }
}
