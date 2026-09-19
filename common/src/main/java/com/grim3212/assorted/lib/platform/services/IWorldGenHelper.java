package com.grim3212.assorted.lib.platform.services;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public interface IWorldGenHelper {

    void addFeatureToBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, Identifier configuredFeatureIdentifier);

    /**
     * Takes a placed feature out of every biome the predicate accepts, through the loader's own
     * biome modifiers rather than by overriding the biome's json. The predicate is asked when a world
     * loads its biomes, so it may read config.
     */
    void removeFeatureFromBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, Identifier placedFeatureIdentifier);

    /**
     * Adds a creature to the natural spawns of every biome the predicate accepts, in the category
     * the type was built with. The predicate and the weight are asked when a world loads its biomes,
     * so both may read config. Pair it with {@link IPlatformHelper#registerSpawnPlacement}, which decides where in
     * those biomes the creature may appear. One call per type: Fabric names the addition after it.
     */
    void addSpawnToBiomes(BiomePredicate biomePredicate, Supplier<? extends EntityType<?>> type, IntSupplier weight, int minCount, int maxCount);

    @FunctionalInterface
    interface BiomePredicate {
        boolean test(Identifier key, Holder<Biome> biomeHolder);
    }
}
