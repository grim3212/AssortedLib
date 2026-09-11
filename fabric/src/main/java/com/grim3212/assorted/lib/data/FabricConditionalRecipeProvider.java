package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Runs a mod's common {@link ConditionalRecipeProvider.Runner} through Fabric's own recipe provider.
 * <p>
 * Fabric only writes a recipe's load conditions - into the recipe json and its unlock advancement -
 * when the recipe goes through {@link FabricRecipeProvider}'s output. A plain
 * {@code RecipeProvider.Runner} serialises the recipe without them, so every conditional recipe
 * loaded unconditionally on Fabric and failed to parse wherever its tags were missing. Register this
 * in a mod's Fabric datagen instead of the common runner.
 */
public class FabricConditionalRecipeProvider extends FabricRecipeProvider {

    private final ConditionalRecipeProvider.Runner common;

    public FabricConditionalRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries, ConditionalRecipeProvider.Runner common) {
        super(output, registries);
        this.common = common;
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return this.common.newProvider(registries, output);
    }

    @Override
    public String getName() {
        return this.common.getName();
    }
}
