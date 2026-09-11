package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public interface IFluidManager {
    /** The fluid and amount stored in the stack, if any. */
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
     * Extracts up to {@code amount} of fluid from one item of the stack, working on a copy.
     * {@code stack} is never modified; the caller decides what to do with the result.
     *
     * @return what the item became (e.g. an empty bucket), or an unchanged copy if nothing moved
     */
    ItemStack extractFrom(final ItemStack stack, final long amount);

    /**
     * How much {@link #extractFrom(ItemStack, long)} would extract, without extracting it.
     */
    long simulateExtract(final ItemStack stack, final long amount);

    /**
     * Inserts the fluid into one item of the stack, working on a copy; {@code stack} is untouched.
     *
     * @return what the item became (e.g. a water bucket), or an unchanged copy if nothing moved
     */
    ItemStack insertInto(ItemStack stack, FluidInformation fluidInformation);

    /**
     * How much {@link #insertInto(ItemStack, FluidInformation)} would insert, without inserting it.
     */
    long simulateInsert(ItemStack stack, FluidInformation fluidInformation);

    /** The amount of fluid in one bucket on this loader. */
    default long getBucketAmount() {
        return 1000;
    }

    /** The fluid's display name. */
    Component getDisplayName(final Fluid fluid);

    /** The fluid's variant handler: empty, or a default handler, depending on the loader. */
    Optional<IFluidVariantHandler> getVariantHandlerFor(final Fluid fluid);

    /** The fluid's variant handler: empty, or a default handler, depending on the loader. */
    default Optional<IFluidVariantHandler> getVariantHandlerFor(FluidInformation fluid) {
        return getVariantHandlerFor(fluid.fluid());
    }
}
