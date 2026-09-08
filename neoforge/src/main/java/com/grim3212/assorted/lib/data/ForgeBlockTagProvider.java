package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ForgeBlockTagProvider extends BlockTagsProvider {

    private final LibBlockTagProvider commonBlocks;

    public ForgeBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper, String modId, LibBlockTagProvider commonBlocks) {
        super(output, lookupProvider, modId, existingFileHelper);
        this.commonBlocks = commonBlocks;
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.commonBlocks.addCommonTags(this::tag);
    }

}