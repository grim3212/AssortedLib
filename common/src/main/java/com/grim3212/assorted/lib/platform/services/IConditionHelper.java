package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.core.conditions.LibCondition;
import net.minecraft.data.recipes.RecipeOutput;
import com.grim3212.assorted.lib.core.conditions.LibConditionProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface IConditionHelper {
    void init();

    /**
     * Wraps a {@link RecipeOutput} so that any recipe written through it carries the conditions
     * registered for its id.
     * <p>
     * This replaces the old {@code write(JsonObject, ...)}. Recipes are no longer serialised to a
     * {@code JsonObject} by the provider - {@code RecipeProvider.Runner} writes them via
     * {@code Recipe.CODEC} - so the only place left to attach conditions is the output itself
     * (NeoForge exposes {@code RecipeOutput#withConditions}, Fabric its own equivalent).
     *
     * @param output     The output to wrap.
     * @param conditions Conditions per recipe id. Read lazily on each accept, so it may still be
     *                   populated after this call.
     */
    RecipeOutput conditionalOutput(RecipeOutput output, Map<Identifier, List<LibConditionProvider>> conditions);

    void register(Identifier name, LibCondition condition);

    LibConditionProvider and(LibConditionProvider... values);

    LibConditionProvider not(LibConditionProvider value);

    LibConditionProvider or(LibConditionProvider... values);

    LibConditionProvider blockExists(Identifier block);

    LibConditionProvider itemExists(Identifier item);

    LibConditionProvider blockTagExists(TagKey<Block> tag);

    LibConditionProvider itemTagExists(TagKey<Item> tag);

    LibConditionProvider modLoaded(String modId);

    LibConditionProvider partEnabled(String partId);

    void registerPartCondition(String part, Supplier<Boolean> check);
}
