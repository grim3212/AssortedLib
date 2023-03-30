package com.grim3212.assorted.lib.platform.services;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;

public interface IWorldGenHelper {

    void addFeatureToBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, ResourceLocation configuredFeatureIdentifier);

    @FunctionalInterface
    interface BiomePredicate {
        boolean test(ResourceLocation key, Holder<Biome> biomeHolder);
    }
}
