package com.grim3212.assorted.lib.core.fluid;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

/**
 * A fluid, an amount of it, and its component patch - the same data a fluid stack carries on both
 * loaders.
 */
public record FluidInformation(Fluid fluid, long amount, DataComponentPatch data) {
    public FluidInformation(Fluid fluid) {
        this(fluid, 1, DataComponentPatch.EMPTY);
    }

    public FluidInformation(Fluid fluid, long amount) {
        this(fluid, amount, DataComponentPatch.EMPTY);
    }

    public FluidInformation withSource() {
        if (fluid instanceof FlowingFluid flowingFluid) {
            return new FluidInformation(flowingFluid.getSource(), amount, data);
        }

        return this;
    }

    public FluidInformation withAmount(long amount) {
        return new FluidInformation(fluid, amount, data);
    }
}
