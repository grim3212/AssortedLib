package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.NotImplementedException;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class LibItemTagProvider extends VanillaItemTagsProvider {

    public LibItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        throw new NotImplementedException();
    }

    @Override
    protected IntrinsicTagAppender<Item> tag(TagKey<Item> tag) {
        throw new NotImplementedException();
    }

    @Override
    protected void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
        throw new NotImplementedException();
    }

    public abstract void addCommonTags(Function<TagKey<Item>, IntrinsicTagAppender<Item>> tagger, BiConsumer<TagKey<Block>, TagKey<Item>> copier);
}
