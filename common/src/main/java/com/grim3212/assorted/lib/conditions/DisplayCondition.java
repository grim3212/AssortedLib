package com.grim3212.assorted.lib.conditions;

import com.mojang.serialization.MapCodec;

/**
 * A test on whether a piece of content should be shown, read from a resource pack the way a
 * recipe's load conditions are read from a data pack.
 */
public interface DisplayCondition {

    boolean test();

    MapCodec<? extends DisplayCondition> codec();
}
