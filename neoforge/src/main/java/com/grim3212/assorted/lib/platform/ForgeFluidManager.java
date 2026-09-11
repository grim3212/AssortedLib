package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
import com.grim3212.assorted.lib.fluid.ForgeFluidVariantHandlerDelegate;
import com.grim3212.assorted.lib.platform.services.IFluidManager;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * {@code ForgeCapabilities.FLUID_HANDLER_ITEM} and {@code IFluidHandler} are gone. The fluid
 * capability of an item is a {@link ResourceHandler} of {@link FluidResource} now: slot indexed,
 * and transactional rather than taking a simulate flag, so a simulation is a {@link Transaction}
 * that is closed without being committed.
 */
public class ForgeFluidManager implements IFluidManager {

    @Override
    public Optional<FluidInformation> get(final ItemStack stack) {
        final SingleItem item = SingleItem.of(stack);
        if (item == null)
            return Optional.empty();

        for (int index = 0; index < item.fluids().size(); index++) {
            final FluidStack contained = FluidUtil.getStack(item.fluids(), index);
            if (!contained.isEmpty()) {
                return Optional.of(buildFluidInformation(contained));
            }
        }

        // The item is a fluid container, it just happens to be empty right now.
        return Optional.of(new FluidInformation(Fluids.EMPTY, 0));
    }

    @Override
    public ItemStack extractFrom(final ItemStack stack, final long amount) {
        final SingleItem item = SingleItem.of(stack);
        if (item == null)
            return stack.copyWithCount(1);

        try (Transaction transaction = Transaction.openRoot()) {
            final ResourceStack<FluidResource> extracted = ResourceHandlerUtil.extractFirst(item.fluids(), resource -> true, clamp(amount), transaction);
            if (extracted != null && extracted.amount() > 0) {
                transaction.commit();
            }
        }
        return item.contents();
    }

    @Override
    public long simulateExtract(ItemStack stack, long amount) {
        final SingleItem item = SingleItem.of(stack);
        if (item == null)
            return 0;

        // Not committing the transaction rolls the extraction back, which is the simulation.
        try (Transaction transaction = Transaction.openRoot()) {
            final ResourceStack<FluidResource> extracted = ResourceHandlerUtil.extractFirst(item.fluids(), resource -> true, clamp(amount), transaction);
            return extracted == null ? 0 : extracted.amount();
        }
    }

    @Override
    public ItemStack insertInto(final ItemStack stack, final FluidInformation fluidInformation) {
        final SingleItem item = SingleItem.of(stack);
        if (item == null)
            return stack.copyWithCount(1);

        if (clamp(fluidInformation.amount()) > 0 && fluidInformation.fluid() != Fluids.EMPTY) {
            try (Transaction transaction = Transaction.openRoot()) {
                if (ResourceHandlerUtil.insertStacking(item.fluids(), FluidResource.of(buildFluidStack(fluidInformation)), clamp(fluidInformation.amount()), transaction) > 0) {
                    transaction.commit();
                }
            }
        }
        return item.contents();
    }

    @Override
    public long simulateInsert(ItemStack stack, FluidInformation fluidInformation) {
        final SingleItem item = SingleItem.of(stack);
        if (item == null || clamp(fluidInformation.amount()) <= 0 || fluidInformation.fluid() == Fluids.EMPTY)
            return 0;

        try (Transaction transaction = Transaction.openRoot()) {
            return ResourceHandlerUtil.insertStacking(item.fluids(), FluidResource.of(buildFluidStack(fluidInformation)), clamp(fluidInformation.amount()), transaction);
        }
    }

    @Override
    public Component getDisplayName(final Fluid fluid) {
        return fluid.getFluidType().getDescription(buildFluidStack(new FluidInformation(fluid)));
    }

    @Override
    public Optional<IFluidVariantHandler> getVariantHandlerFor(Fluid fluid) {
        return Optional.of(new ForgeFluidVariantHandlerDelegate(fluid.getFluidType()));
    }

    private static int clamp(final long amount) {
        return (int) Math.max(0, Math.min(amount, Integer.MAX_VALUE));
    }

    // FluidInformation carries the same DataComponentPatch a FluidStack does, so both directions are
    // a straight copy.
    @NotNull
    public static FluidInformation buildFluidInformation(final FluidStack fluid) {
        return new FluidInformation(fluid.getFluid(), fluid.getAmount(), fluid.getComponentsPatch());
    }

    @NotNull
    public static FluidStack buildFluidStack(final FluidInformation fluid) {
        return new FluidStack(fluid.fluid(), clamp(fluid.amount()), fluid.data());
    }

    /**
     * One item out of the stack, in a one-slot handler that its fluid capability can write to.
     * <p>
     * {@code ItemAccess.forStack} mutates the stack in place and can never change its item, so a
     * water bucket - which empties by becoming a different item - reports nothing extractable
     * through it. {@link ItemAccess#forHandlerIndexStrict} over a slot of our own can swap the item,
     * and the slot is read back after the transaction.
     */
    private record SingleItem(ItemStacksResourceHandler slot, ResourceHandler<FluidResource> fluids) {
        @Nullable
        static SingleItem of(final ItemStack stack) {
            if (stack.isEmpty())
                return null;

            final ItemStacksResourceHandler slot = new ItemStacksResourceHandler(NonNullList.of(ItemStack.EMPTY, stack.copyWithCount(1)));
            final ResourceHandler<FluidResource> fluids = ItemAccess.forHandlerIndexStrict(slot, 0).getCapability(Capabilities.Fluid.ITEM);
            return fluids == null ? null : new SingleItem(slot, fluids);
        }

        ItemStack contents() {
            return this.slot.getResource(0).toStack((int) this.slot.getAmountAsLong(0));
        }
    }
}
