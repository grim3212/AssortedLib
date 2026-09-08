package com.grim3212.assorted.lib.core.conditions;

/**
 * A named condition's test.
 * <p>
 * The 1.20.1 version took the condition's {@code JsonObject} so it could read its own parameters.
 * Conditions are codec-deserialised on both loaders now, so a condition's parameters are already
 * bound by the time it is tested and this is a plain predicate.
 */
public interface LibCondition {

    boolean test();
}
