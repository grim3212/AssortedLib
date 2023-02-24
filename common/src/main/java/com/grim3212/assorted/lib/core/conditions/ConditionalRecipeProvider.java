package com.grim3212.assorted.lib.core.conditions;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class ConditionalRecipeProvider extends RecipeProvider {

    protected final Map<ResourceLocation, List<LibConditionProvider>> conditions;
    protected final PackOutput.PathProvider recipePathProvider;
    protected final PackOutput.PathProvider advancementPathProvider;
    private final String modId;

    public ConditionalRecipeProvider(PackOutput output, String modId) {
        super(output);
        this.modId = modId;
        this.conditions = new HashMap<>();
        this.recipePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipes");
        this.advancementPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
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

    protected ResourceLocation id(Item i) {
        return Services.PLATFORM.getRegistry(Registries.ITEM).getRegistryName(i);
    }

    protected ResourceLocation id(Block b) {
        return Services.PLATFORM.getRegistry(Registries.BLOCK).getRegistryName(b);
    }

    protected ResourceLocation prefix(String name) {
        return new ResourceLocation(this.modId, name);
    }

    public void addConditions(LibConditionProvider condition, ResourceLocation... recipes) {
        if (recipes.length == 0)
            return;

        for (ResourceLocation recipe : recipes) {
            this.conditions.computeIfAbsent(recipe, (r) -> new ArrayList<>()).add(condition);
        }
    }

    public void writeConditions(ResourceLocation id, JsonObject json) {
        if (this.conditions.containsKey(id))
            Services.CONDITIONS.write(json, this.conditions.get(id).toArray(new LibConditionProvider[0]));
    }

    /**
     * Used to register the conditions for recipes before the recipes are built
     * Makes sure that the conditions are available when building the recipe JSONs
     */
    public abstract void registerConditions();

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        this.registerConditions();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Set<ResourceLocation> recipes = Sets.newHashSet();
        List<CompletableFuture<?>> finishedRecipes = new ArrayList<>();
        this.buildRecipes((curRecipe) -> {
            if (!recipes.add(curRecipe.getId())) {
                throw new IllegalStateException("Duplicate recipe " + curRecipe.getId());
            } else {
                JsonObject recipeJson = curRecipe.serializeRecipe();
                this.writeConditions(curRecipe.getId(), recipeJson);
                finishedRecipes.add(DataProvider.saveStable(output, recipeJson, this.recipePathProvider.json(curRecipe.getId())));
                JsonObject advancementJson = curRecipe.serializeAdvancement();
                if (advancementJson != null) {
                    this.writeConditions(curRecipe.getId(), advancementJson);
                    finishedRecipes.add(DataProvider.saveStable(output, advancementJson, this.advancementPathProvider.json(curRecipe.getAdvancementId())));
                }

            }
        });
        return CompletableFuture.allOf(finishedRecipes.toArray(($$0x) -> new CompletableFuture[$$0x]));
    }
}
