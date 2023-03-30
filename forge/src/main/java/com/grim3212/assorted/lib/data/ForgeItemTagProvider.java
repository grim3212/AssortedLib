package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ForgeItemTagProvider extends ItemTagsProvider {

    private final LibItemTagProvider commonItems;

    public ForgeItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, TagsProvider<Block> blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper, String modId, LibItemTagProvider commonItems) {
        super(pOutput, pLookupProvider, blockTagsProvider, modId, existingFileHelper);
        this.commonItems = commonItems;
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.commonItems.addCommonTags(this::tag, (pair) -> this.copy(pair.getA(), pair.getB()));
    }

}