package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.apache.commons.lang3.NotImplementedException;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class LibEntityTagProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {

    public LibEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ENTITY_TYPE, lookupProvider, (type) -> type.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        throw new NotImplementedException();
    }

    @Override
    protected IntrinsicTagAppender<EntityType<?>> tag(TagKey<EntityType<?>> tag) {
        throw new NotImplementedException();
    }

    public abstract void addCommonTags(Function<TagKey<EntityType<?>>, IntrinsicTagAppender<EntityType<?>>> tagger);
}
