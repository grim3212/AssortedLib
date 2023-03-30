package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.annotations.LoaderImplement;
import com.grim3212.assorted.lib.mixin.data.AccessorBlockLootSubProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class LibBlockLootProvider extends VanillaBlockLoot {

    private final Supplier<Iterable<Block>> knownBlocks;


    public LibBlockLootProvider(Supplier<Iterable<Block>> knownBlocks) {
        this.knownBlocks = knownBlocks;
    }

    @Override
    public abstract void generate();

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        this.generate();
        Set<ResourceLocation> set = new HashSet<>();
        AccessorBlockLootSubProvider provider = ((AccessorBlockLootSubProvider) this);

        for (Block block : getKnownBlocks()) {
            if (block.isEnabled(provider.assortedlib_getEnabledFeatures())) {
                ResourceLocation resourcelocation = block.getLootTable();
                if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
                    LootTable.Builder loottable$builder = provider.assortedlib_getMap().remove(resourcelocation);
                    if (loottable$builder == null) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourcelocation, BuiltInRegistries.BLOCK.getKey(block)));
                    }

                    biConsumer.accept(resourcelocation, loottable$builder);
                }
            }
        }

        if (!provider.assortedlib_getMap().isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + provider.assortedlib_getMap().keySet());
        }
    }

    @LoaderImplement(loader = LoaderImplement.Loader.FORGE, value = "BlockLootSubProvider")
    protected Iterable<Block> getKnownBlocks() {
        return this.knownBlocks.get();
    }

}