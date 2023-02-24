package com.grim3212.assorted.lib.core.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

public record FluidInformation(Fluid fluid, long amount, CompoundTag data) {
    public FluidInformation(Fluid fluid) {
        this(fluid, 1, new CompoundTag());
    }

    public FluidInformation(Fluid fluid, long amount) {
        this(fluid, amount, new CompoundTag());
    }
}
