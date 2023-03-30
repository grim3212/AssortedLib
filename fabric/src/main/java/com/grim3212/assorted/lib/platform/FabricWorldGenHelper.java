package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IWorldGenHelper;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;

public class FabricWorldGenHelper implements IWorldGenHelper {
    @Override
    public void addFeatureToBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, ResourceLocation placedFeatureIdentifier) {
        BiomeModifications.addFeature(it -> biomePredicate.test(it.getBiomeKey().location(), it.getBiomeRegistryEntry()), step, ResourceKey.create(Registries.PLACED_FEATURE, placedFeatureIdentifier));
    }
}
