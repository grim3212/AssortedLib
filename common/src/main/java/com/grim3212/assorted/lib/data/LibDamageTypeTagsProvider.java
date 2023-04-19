package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.apache.commons.lang3.NotImplementedException;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class LibDamageTypeTagsProvider extends DamageTypeTagsProvider {
    public LibDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected TagAppender<DamageType> tag(TagKey<DamageType> tag) {
        throw new NotImplementedException();
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        throw new NotImplementedException();
    }

    public abstract void addCommonTags(Function<TagKey<DamageType>, TagAppender<DamageType>> tagger);

}
