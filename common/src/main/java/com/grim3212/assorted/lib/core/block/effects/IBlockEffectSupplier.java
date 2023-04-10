package com.grim3212.assorted.lib.core.block.effects;

import java.util.function.Supplier;

public interface IBlockEffectSupplier {
    Supplier<IBlockClientEffects> getClientEffects();
}
