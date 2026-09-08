package com.grim3212.assorted.lib.platform.services;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;

public interface IWorldGenHelper {

    void addFeatureToBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, Identifier configuredFeatureIdentifier);

    @FunctionalInterface
    interface BiomePredicate {
        boolean test(Identifier key, Holder<Biome> biomeHolder);
    }
}
