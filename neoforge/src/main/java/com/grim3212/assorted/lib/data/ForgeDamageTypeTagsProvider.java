package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ForgeDamageTypeTagsProvider extends DamageTypeTagsProvider {

    private final LibDamageTypeTagsProvider commonTags;

    public ForgeDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, LibDamageTypeTagsProvider commonTags) {
        super(output, lookup);
        this.commonTags = commonTags;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.commonTags.addCommonTags(this::tag);
    }
}
