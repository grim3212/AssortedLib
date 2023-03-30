package com.grim3212.assorted.lib.worldgen;

import com.grim3212.assorted.lib.platform.services.IWorldGenHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class BiomeModification {
    private final IWorldGenHelper.BiomePredicate biomePredicate;
    private final GenerationStep.Decoration step;
    private final ResourceKey<PlacedFeature> placedFeatureKey;

    public BiomeModification(IWorldGenHelper.BiomePredicate biomePredicate, GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey) {
        this.biomePredicate = biomePredicate;
        this.step = step;
        this.placedFeatureKey = placedFeatureKey;
    }

    public IWorldGenHelper.BiomePredicate getBiomePredicate() {
        return biomePredicate;
    }

    public GenerationStep.Decoration getStep() {
        return step;
    }

    public ResourceKey<PlacedFeature> getConfiguredFeatureKey() {
        return placedFeatureKey;
    }
}
