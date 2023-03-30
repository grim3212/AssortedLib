package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;

import java.util.concurrent.CompletableFuture;

public class FabricEntityTagProvider extends EntityTypeTagsProvider {

    private final LibEntityTagProvider commonEntities;

    public FabricEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, LibEntityTagProvider commonEntities) {
        super(output, lookup);
        this.commonEntities = commonEntities;
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonEntities.addCommonTags(this::tag);
    }
}
