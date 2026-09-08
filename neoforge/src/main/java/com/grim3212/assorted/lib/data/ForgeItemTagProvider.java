package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Block to item tag copying moved off the vanilla item tag provider in 26.2; NeoForge keeps it on
 * {@link BlockTagCopyingItemTagProvider}, which is what this provider extends now.
 */
public class ForgeItemTagProvider extends BlockTagCopyingItemTagProvider {

    private final LibItemTagProvider commonItems;

    public ForgeItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider, String modId, LibItemTagProvider commonItems) {
        super(pOutput, pLookupProvider, blockTagsProvider, modId);
        this.commonItems = commonItems;
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.commonItems.addCommonTags(this::tag, this::copy);
    }

}
