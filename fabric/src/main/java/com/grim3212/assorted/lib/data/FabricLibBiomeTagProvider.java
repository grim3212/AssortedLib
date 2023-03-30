package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;

import java.util.concurrent.CompletableFuture;

public class FabricLibBiomeTagProvider extends BiomeTagsProvider {

    private final LibCommonTagProvider.BiomeTagProvider commonBiomes;

    public FabricLibBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.commonBiomes = new LibCommonTagProvider.BiomeTagProvider(output, lookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonBiomes.addCommonTags(this::tag, false);
    }
}
