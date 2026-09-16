package com.grim3212.assorted.lib.platform.services;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;

public interface IWorldGenHelper {

    void addFeatureToBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, Identifier configuredFeatureIdentifier);

    /**
     * Takes a placed feature out of every biome the predicate accepts, through the loader's own
     * biome modifiers rather than by overriding the biome's json. The predicate is asked when a world
     * loads its biomes, so it may read config.
     */
    void removeFeatureFromBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, Identifier placedFeatureIdentifier);

    @FunctionalInterface
    interface BiomePredicate {
        boolean test(Identifier key, Holder<Biome> biomeHolder);
    }
}
