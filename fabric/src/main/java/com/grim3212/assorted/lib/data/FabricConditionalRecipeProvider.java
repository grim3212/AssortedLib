package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Runs a mod's common {@link ConditionalRecipeProvider.Runner} through {@link
 * FabricRecipeProvider}, the only output that writes load conditions into the recipe and its
 * advancement. Without it every conditional recipe loads unconditionally on Fabric. Use it in
 * Fabric datagen instead of the common runner.
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
