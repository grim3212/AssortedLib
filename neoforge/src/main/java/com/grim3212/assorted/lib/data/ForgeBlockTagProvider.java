package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ForgeBlockTagProvider extends BlockTagsProvider {

    private final LibBlockTagProvider commonBlocks;

    public ForgeBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, LibBlockTagProvider commonBlocks) {
        super(output, lookupProvider, modId);
        this.commonBlocks = commonBlocks;
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.commonBlocks.addCommonTags(this::tag);
    }

}
