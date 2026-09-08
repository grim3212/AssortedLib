package com.grim3212.assorted.lib.core.conditions;

import net.minecraft.resources.Identifier;

/**
 * A loader-agnostic handle on one recipe condition.
 * <p>
 * Conditions used to be written straight into a recipe's JSON, so this interface used to expose a
 * {@code write(JsonObject)}. In 26.2 recipes are serialised through {@code Recipe.CODEC} by
 * {@code RecipeProvider.Runner} and conditions are attached by wrapping the {@code RecipeOutput}
 * instead, so this is now an opaque token: each loader implements it over its own condition type
 * ({@code ICondition} on NeoForge, {@code ResourceCondition} on Fabric) and unwraps it in
 * {@code IConditionHelper#conditionalOutput}.
 */
public interface LibConditionProvider {

    Identifier getName();
}
