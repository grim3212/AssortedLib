package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class FabricItemTagProvider extends VanillaItemTagsProvider {

    private final LibItemTagProvider commonItems;

    public FabricItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTagsProvider, LibItemTagProvider commonItems) {
        super(output, lookup, blockTagsProvider);
        this.commonItems = commonItems;
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonItems.addCommonTags(this::tag, this::copy);
    }
}
