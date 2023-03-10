package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaBlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class FabricLibBlockTagProvider extends VanillaBlockTagsProvider {

    private final LibCommonTagProvider.BlockTagProvider commonBlocks;

    public FabricLibBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.commonBlocks = new LibCommonTagProvider.BlockTagProvider(output, lookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonBlocks.addCommonTags(this::tag, false);
    }
}
