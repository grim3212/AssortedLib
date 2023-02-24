package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class FabricLibItemTagProvider extends VanillaItemTagsProvider {

    private final LibCommonTagProvider.ItemTagProvider commonItems;

    public FabricLibItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> blockTagsProvider) {
        super(output, lookup, blockTagsProvider);
        this.commonItems = new LibCommonTagProvider.ItemTagProvider(output, lookup, blockTagsProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonItems.addCommonTags(this::tag, (pair) -> this.copy(pair.getA(), pair.getB()), false);
    }
}
