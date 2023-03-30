package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaBlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class FabricBlockTagProvider extends VanillaBlockTagsProvider {

    private final LibBlockTagProvider commonBlocks;

    public FabricBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, LibBlockTagProvider commonBlocks) {
        super(output, lookup);
        this.commonBlocks = commonBlocks;
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonBlocks.addCommonTags(this::tag);
    }
}
