package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.LibConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ForgeLibBlockTagProvider extends BlockTagsProvider {

    private final LibCommonTagProvider.BlockTagProvider commonBlocks;

    public ForgeLibBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, LibConstants.MOD_ID, existingFileHelper);
        this.commonBlocks = new LibCommonTagProvider.BlockTagProvider(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.commonBlocks.addCommonTags(this::tag, true);
    }

}
