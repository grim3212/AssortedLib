package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public interface IFluidManager {
    /**
     * Returns the fluid, and the amount of fluid stored in the given itemstack.
     *
     * @param stack The item stack to get the fluid from.
     * @return An optional possibly containing the fluid and the amount of fluid in the stack.
     */
    Optional<FluidInformation> get(final ItemStack stack);

    /**
     * Extracts all the fluid from the itemstack.
     *
     * @param stack The itemstack to extract all fluids from.
     */
    default ItemStack extractFrom(final ItemStack stack) {
        return extractFrom(stack, Long.MAX_VALUE);
    }

    /**
     * Extracts the given amount of fluids from the given stack.
     *
     * @param stack  The stack to extract from.
     * @param amount The amount to extract.
     * @return The resulting itemstack from the extraction.
     */
    ItemStack extractFrom(final ItemStack stack, final long amount);

    long simulateExtract(final ItemStack stack, final long amount);

    /**
     * Invoked to insert a given amount of fluid into the given stack.
     *
     * @param stack            The stack to insert into.
     * @param fluidInformation The fluid to insert.
     * @return The resulting stack of the insertion.
     */
    ItemStack insertInto(ItemStack stack, FluidInformation fluidInformation);

    long simulateInsert(ItemStack stack, FluidInformation fluidInformation);

    /**
     * The amount of a fluid in a single bucket on a given platform.
     * In general this is 1000 mB, which is equal to one block.
     *
     * @return The amount of fluid in one bucket.
     */
    default long getBucketAmount() {
        return 1000;
    }

    /**
     * Gives access to the color of the fluid.
     *
     * @param fluid The fluid.
     * @return The color packed into an int.
     */
    int getFluidColor(FluidInformation fluid);

    /**
     * Gives access to the color of the fluid.
     *
     * @param fluid The fluid.
     * @return The color packed into an int.
     */
    default int getFluidColor(final Fluid fluid) {
        return getFluidColor(new FluidInformation(fluid));
    }

    /**
     * Gets the display name of the fluid.
     *
     * @param fluid The fluid to get the display name from.
     * @return The display name of the fluid.
     */
    Component getDisplayName(final Fluid fluid);

    String fluidStackTag();
}
