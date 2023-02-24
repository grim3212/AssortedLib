package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.LibConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ForgeLibItemTagProvider extends ItemTagsProvider {

    private final LibCommonTagProvider.ItemTagProvider commonItems;

    public ForgeLibItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, TagsProvider<Block> blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, blockTagsProvider, LibConstants.MOD_ID, existingFileHelper);
        this.commonItems = new LibCommonTagProvider.ItemTagProvider(pOutput, pLookupProvider, blockTagsProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.commonItems.addCommonTags(this::tag, (pair) -> this.copy(pair.getA(), pair.getB()), true);
    }
}
