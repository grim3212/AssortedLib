package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IWorldGenHelper;
import com.grim3212.assorted.lib.worldgen.BiomeModification;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ForgeWorldGenHelper implements IWorldGenHelper {
    private static final List<BiomeModification> biomeModifications = new ArrayList<>();
    private static final List<BiomeModification> biomeRemovals = new ArrayList<>();

    /**
     * Kept in the order Fabric applies its own modifications in, which is by the placed feature's id
     * - {@code Identifier} sorts on path first, then namespace. A feature's position in its step
     * decides the seed it generates from, so adding the same features in a different order gives the
     * same seed two different worlds: the terrain matches and every scattered feature has moved.
     * Registration order cannot be used, because on Fabric it is the mod loading order that would
     * have to match and that is not ours to set.
     */
    private static final Comparator<BiomeModification> FABRIC_ORDER =
            Comparator.comparing(modification -> modification.getConfiguredFeatureKey().identifier());

    @Override
    public void addFeatureToBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, Identifier placedFeatureIdentifier) {
        ResourceKey<PlacedFeature> resourceKey = ResourceKey.create(Registries.PLACED_FEATURE, placedFeatureIdentifier);
        biomeModifications.add(new BiomeModification(biomePredicate, step, resourceKey));
        biomeModifications.sort(FABRIC_ORDER);
    }

    @Override
    public void removeFeatureFromBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, Identifier placedFeatureIdentifier) {
        ResourceKey<PlacedFeature> resourceKey = ResourceKey.create(Registries.PLACED_FEATURE, placedFeatureIdentifier);
        biomeRemovals.add(new BiomeModification(biomePredicate, step, resourceKey));
        biomeRemovals.sort(FABRIC_ORDER);
    }

    public static void modifyBiome(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == BiomeModifier.Phase.REMOVE) {
            Identifier location = biome.unwrapKey().map(ResourceKey::identifier).orElse(null);
            for (var removal : biomeRemovals) {
                if (location != null && removal.getBiomePredicate().test(location, biome)) {
                    builder.getGenerationSettings().getFeatures(removal.getStep()).removeIf(feature -> feature.is(removal.getConfiguredFeatureKey()));
                }
            }
        }

        if (phase == BiomeModifier.Phase.ADD) {
            for (var biomeModification : biomeModifications) {
                Identifier location = biome.unwrapKey().map(ResourceKey::identifier).orElse(null);
                if (location != null && biomeModification.getBiomePredicate().test(location, biome)) {
                    Registry<PlacedFeature> placedFeatures = ServerLifecycleHooks.getCurrentServer()
                            .registryAccess()
                            .lookupOrThrow(Registries.PLACED_FEATURE);
                    placedFeatures.get(biomeModification.getConfiguredFeatureKey())
                            .ifPresent(placedFeature -> builder.getGenerationSettings().addFeature(biomeModification.getStep(), placedFeature));
                }
            }
        }
    }
}
