package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.platform.services.IWorldGenHelper;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.concurrent.atomic.AtomicInteger;

public class FabricWorldGenHelper implements IWorldGenHelper {

    /** Fabric names every modification; this keeps two removals of the same feature apart. */
    private static final AtomicInteger REMOVALS = new AtomicInteger();

    @Override
    public void addFeatureToBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, Identifier placedFeatureIdentifier) {
        BiomeModifications.addFeature(it -> biomePredicate.test(it.getBiomeKey().identifier(), it.getBiomeHolder()), step, ResourceKey.create(Registries.PLACED_FEATURE, placedFeatureIdentifier));
    }

    @Override
    public void removeFeatureFromBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, Identifier placedFeatureIdentifier) {
        ResourceKey<PlacedFeature> key = ResourceKey.create(Registries.PLACED_FEATURE, placedFeatureIdentifier);
        Identifier name = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "remove_" + REMOVALS.getAndIncrement() + "_" + placedFeatureIdentifier.getNamespace() + "_" + placedFeatureIdentifier.getPath().replace('/', '_'));
        BiomeModifications.create(name).add(ModificationPhase.REMOVALS, it -> biomePredicate.test(it.getBiomeKey().identifier(), it.getBiomeHolder()), context -> context.getGenerationSettings().removeFeature(step, key));
    }
}
