package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.LibConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ForgeLibBiomeTagProvider extends BiomeTagsProvider {

    private final LibCommonTagProvider.BiomeTagProvider commonBiomes;

    public ForgeLibBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookup, LibConstants.MOD_ID, existingFileHelper);
        this.commonBiomes = new LibCommonTagProvider.BiomeTagProvider(output, lookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonBiomes.addCommonTags(this::tag, true);
    }
}
