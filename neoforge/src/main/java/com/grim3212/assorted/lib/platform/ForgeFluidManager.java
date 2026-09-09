package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
import com.grim3212.assorted.lib.fluid.ForgeFluidVariantHandlerDelegate;
import com.grim3212.assorted.lib.platform.services.IFluidManager;
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
        final ResourceHandler<FluidResource> handler = fluidHandler(stack);
        if (handler == null)
            return Optional.empty();

        for (int index = 0; index < handler.size(); index++) {
            final FluidStack contained = FluidUtil.getStack(handler, index);
            if (!contained.isEmpty()) {
                return Optional.of(buildFluidInformation(contained));
            }
        }

        // The item is a fluid container, it just happens to be empty right now.
        return Optional.of(new FluidInformation(Fluids.EMPTY, 0));
    }

    @Override
    public ItemStack extractFrom(final ItemStack stack, final long amount) {
        final ResourceHandler<FluidResource> handler = fluidHandler(stack);
        if (handler != null) {
            try (Transaction transaction = Transaction.openRoot()) {
                ResourceHandlerUtil.extractFirst(handler, resource -> true, clamp(amount), transaction);
                transaction.commit();
            }
        }
        return stack;
    }

    @Override
    public long simulateExtract(ItemStack stack, long amount) {
        final ResourceHandler<FluidResource> handler = fluidHandler(stack);
        if (handler == null)
            return 0;

        // Not committing the transaction rolls the extraction back, which is the simulation.
        try (Transaction transaction = Transaction.openRoot()) {
            final ResourceStack<FluidResource> extracted = ResourceHandlerUtil.extractFirst(handler, resource -> true, clamp(amount), transaction);
            return extracted == null ? 0 : extracted.amount();
        }
    }

    @Override
    public ItemStack insertInto(final ItemStack stack, final FluidInformation fluidInformation) {
        final ResourceHandler<FluidResource> handler = fluidHandler(stack);
        if (handler != null && clamp(fluidInformation.amount()) > 0) {
            try (Transaction transaction = Transaction.openRoot()) {
                ResourceHandlerUtil.insertStacking(handler, FluidResource.of(buildFluidStack(fluidInformation)), clamp(fluidInformation.amount()), transaction);
                transaction.commit();
            }
        }
        return stack;
    }

    @Override
    public long simulateInsert(ItemStack stack, FluidInformation fluidInformation) {
        final ResourceHandler<FluidResource> handler = fluidHandler(stack);
        if (handler == null || clamp(fluidInformation.amount()) <= 0)
            return 0;

        try (Transaction transaction = Transaction.openRoot()) {
            return ResourceHandlerUtil.insertStacking(handler, FluidResource.of(buildFluidStack(fluidInformation)), clamp(fluidInformation.amount()), transaction);
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

    /**
     * The fluid handler of an item stack, or {@code null} when it has none. {@code oneByOne()} scopes
     * the access to a single item out of the stack, which is what a fluid container item stores in.
     */
    @Nullable
    private static ResourceHandler<FluidResource> fluidHandler(final ItemStack stack) {
        return ItemAccess.forStack(stack).oneByOne().getCapability(Capabilities.Fluid.ITEM);
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
}
