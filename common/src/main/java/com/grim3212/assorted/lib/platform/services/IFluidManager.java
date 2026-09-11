package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
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
     * Extracts up to the given amount of fluid from one item of the given stack.
     * <p>
     * Works on a copy of a single item and never modifies {@code stack} itself; the caller decides
     * what to do with the result, e.g. shrink the stack by one and give the result back.
     *
     * @param stack  The stack to extract from.
     * @param amount The amount to extract.
     * @return What that one item became, e.g. an empty bucket. An unchanged copy of it when nothing
     * could be extracted.
     */
    ItemStack extractFrom(final ItemStack stack, final long amount);

    /**
     * How much {@link #extractFrom(ItemStack, long)} would extract, without extracting it.
     */
    long simulateExtract(final ItemStack stack, final long amount);

    /**
     * Inserts the given fluid into one item of the given stack.
     * <p>
     * Works on a copy of a single item and never modifies {@code stack} itself.
     *
     * @param stack            The stack to insert into.
     * @param fluidInformation The fluid to insert.
     * @return What that one item became, e.g. a water bucket. An unchanged copy of it when nothing
     * could be inserted.
     */
    ItemStack insertInto(ItemStack stack, FluidInformation fluidInformation);

    /**
     * How much {@link #insertInto(ItemStack, FluidInformation)} would insert, without inserting it.
     */
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
     * Gets the display name of the fluid.
     *
     * @param fluid The fluid to get the display name from.
     * @return The display name of the fluid.
     */
    Component getDisplayName(final Fluid fluid);

    /**
     * Returns the fluid variant handler for the given fluid.
     * This might be empty or filled with a default handler depending on the platform.
     *
     * @param fluid The fluid to get the handler for.
     * @return The fluid variant handler for the given fluid.
     */
    Optional<IFluidVariantHandler> getVariantHandlerFor(final Fluid fluid);

    /**
     * Returns the fluid variant handler for the given fluid information.
     * This might be empty or filled with a default handler depending on the platform.
     *
     * @param fluid The fluid information to get the handler for.
     * @return The fluid variant handler for the given fluid information.
     */
    default Optional<IFluidVariantHandler> getVariantHandlerFor(FluidInformation fluid) {
        return getVariantHandlerFor(fluid.fluid());
    }
}
