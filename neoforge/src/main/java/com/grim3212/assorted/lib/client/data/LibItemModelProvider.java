package com.grim3212.assorted.lib.client.data;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.manual.LibItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/** The library registers no blocks, so this only has the manual to model. */
public class LibItemModelProvider extends ModelProvider {

    public LibItemModelProvider(PackOutput output) {
        super(output, LibConstants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Lib item models";
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(LibItems.INSTRUCTION_MANUAL.get(), ModelTemplates.FLAT_ITEM);
    }
}
