package com.grim3212.assorted.lib.core.conditions;

/**
 * A named condition's test. Conditions are codec-deserialised on both loaders, so its parameters
 * are already bound.
 */
public interface LibCondition {

    boolean test();
}
