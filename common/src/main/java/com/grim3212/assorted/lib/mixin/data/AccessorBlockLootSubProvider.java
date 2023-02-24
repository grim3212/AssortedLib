package com.grim3212.assorted.lib.mixin.data;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockLootSubProvider.class)
public interface AccessorBlockLootSubProvider {

    @Accessor("map")
    Map<ResourceLocation, LootTable.Builder> assortedlib_getMap();

    @Accessor("enabledFeatures")
    FeatureFlagSet assortedlib_getEnabledFeatures();

}
