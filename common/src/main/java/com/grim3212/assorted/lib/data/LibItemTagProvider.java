package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.NotImplementedException;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class LibItemTagProvider extends VanillaItemTagsProvider {

    /**
     * The block tag lookup is no longer accepted by the vanilla item tag provider - block to item
     * tag copying lives on the loader providers now - but it is kept on this constructor so the
     * loader side providers can keep handing their block tag contents getter through unchanged.
     */
    public LibItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        throw new NotImplementedException();
    }

    public abstract void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> tagger, BiConsumer<TagKey<Block>, TagKey<Item>> copier);
}
