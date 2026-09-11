package com.grim3212.assorted.lib.data;

import com.google.common.collect.Maps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FabricItemTagProvider extends VanillaItemTagsProvider {

    private final LibItemTagProvider commonItems;
    private final CompletableFuture<TagLookup<Block>> blockTags;

    /**
     * Block tag to item tag copies collected during {@link #addTags(HolderLookup.Provider)}.
     * Vanilla has no {@code copy(blockTag, itemTag)} any more, so the block builders' entries are
     * spliced into the item builders once the block provider has published its contents.
     */
    private final Map<TagKey<Block>, TagKey<Item>> copies = Maps.newLinkedHashMap();

    public FabricItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTagsProvider, LibItemTagProvider commonItems) {
        super(output, lookup);
        this.blockTags = blockTagsProvider;
        this.commonItems = commonItems;
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.copies.clear();
        this.commonItems.addCommonTags(this::tag, this.copies::put);
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
        return super.createContentsProvider().thenCombine(this.blockTags, (provider, blockLookup) -> {
            this.copies.forEach((blockTag, itemTag) -> {
                final TagBuilder blockBuilder = blockLookup.apply(blockTag).orElseThrow(() -> new IllegalStateException("Missing block tag to copy from: " + blockTag.location()));
                final TagBuilder itemBuilder = this.getOrCreateRawBuilder(itemTag);
                blockBuilder.build().forEach(itemBuilder::add);
            });

            return provider;
        });
    }
}
