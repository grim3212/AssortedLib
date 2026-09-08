package com.grim3212.assorted.lib.core.conditions;

import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class ConditionalRecipeProvider extends RecipeProvider {

    protected final Map<Identifier, List<LibConditionProvider>> conditions;
    private final String modId;

    public ConditionalRecipeProvider(HolderLookup.Provider registries, RecipeOutput output, String modId) {
        this(registries, output, modId, new HashMap<>());
    }

    // The map has to exist before the super call so the wrapped output can close over it, which is
    // why this goes through a private constructor. The wrapper reads it lazily on each accept, so
    // registerConditions() populating it later still works.
    private ConditionalRecipeProvider(HolderLookup.Provider registries, RecipeOutput output, String modId, Map<Identifier, List<LibConditionProvider>> conditions) {
        super(registries, Services.CONDITIONS.conditionalOutput(output, conditions));
        this.modId = modId;
        this.conditions = conditions;
    }

    public LibConditionProvider and(LibConditionProvider... providers) {
        return Services.CONDITIONS.and(providers);
    }

    public LibConditionProvider or(LibConditionProvider... providers) {
        return Services.CONDITIONS.or(providers);
    }

    public LibConditionProvider not(LibConditionProvider provider) {
        return Services.CONDITIONS.not(provider);
    }

    public LibConditionProvider itemTagExists(TagKey<Item> itemTag) {
        return Services.CONDITIONS.itemTagExists(itemTag);
    }

    public LibConditionProvider modLoaded(String modId) {
        return Services.CONDITIONS.modLoaded(modId);
    }

    public LibConditionProvider partEnabled(String part) {
        return Services.CONDITIONS.partEnabled(part);
    }

    public Ingredient and(Ingredient... ingredients) {
        return Services.INGREDIENTS.and(ingredients);
    }

    public Ingredient or(Ingredient... ingredients) {
        return Services.INGREDIENTS.or(ingredients);
    }

    public Ingredient difference(Ingredient base, Ingredient subtracted) {
        return Services.INGREDIENTS.difference(base, subtracted);
    }

    public Ingredient nbt(ItemStack item) {
        return Services.INGREDIENTS.nbt(item);
    }

    public Ingredient fluid(@Nullable TagKey<Item> itemTagKey, TagKey<Fluid> fluidTagKey, long amount) {
        return Services.INGREDIENTS.fluid(itemTagKey, fluidTagKey, amount);
    }

    public Ingredient fluid(TagKey<Fluid> fluidTagKey) {
        return Services.INGREDIENTS.fluid(null, fluidTagKey, Services.FLUIDS.getBucketAmount());
    }

    protected String name(Item i) {
        return id(i).getPath();
    }

    protected String name(Block b) {
        return id(b).getPath();
    }

    protected Identifier id(Item i) {
        return Services.PLATFORM.getRegistry(Registries.ITEM).getRegistryName(i);
    }

    protected Identifier id(Block b) {
        return Services.PLATFORM.getRegistry(Registries.BLOCK).getRegistryName(b);
    }

    protected Identifier prefix(String name) {
        return Identifier.fromNamespaceAndPath(this.modId, name);
    }

    public void addConditions(LibConditionProvider condition, Identifier... recipes) {
        if (recipes.length == 0)
            return;

        for (Identifier recipe : recipes) {
            this.conditions.computeIfAbsent(recipe, (r) -> new ArrayList<>()).add(condition);
        }
    }

    /**
     * Used to register the conditions for recipes before the recipes are built
     * Makes sure that the conditions are available when building the recipe JSONs
     */
    public abstract void registerConditions();

    @Override
    protected void buildRecipes() {
        this.registerConditions();
    }

    /**
     * A {@link RecipeProvider.Runner} that knows the mod id of the provider it creates. Recipe
     * providers are no longer data providers themselves in 26.2 - the runner owns the file
     * writing and hands a {@link RecipeOutput} to a freshly created provider instead.
     */
    public abstract static class Runner extends RecipeProvider.Runner {
        protected final String modId;

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modId) {
            super(output, registries);
            this.modId = modId;
        }
    }
}
