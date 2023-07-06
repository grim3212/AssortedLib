package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
import com.grim3212.assorted.lib.fluid.FabricFluidVariantHandlerDelegate;
import com.grim3212.assorted.lib.platform.services.IFluidManager;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

@SuppressWarnings("removal")
public class FabricFluidManager implements IFluidManager {
    @Override
    public Optional<FluidInformation> get(final ItemStack stack) {
        final Storage<FluidVariant> target = FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
        if (target == null)
            return Optional.empty();

        if (!target.iterator().hasNext())
            return Optional.empty();

        final StorageView<FluidVariant> view = target.iterator().next();

        return Optional.of(
                new FluidInformation(
                        view.getResource().getFluid(),
                        view.getAmount(),
                        view.getResource().copyNbt()
                )
        );
    }

    @Override
    public ItemStack extractFrom(final ItemStack stack, final long amount) {
        try (final Transaction context = Transaction.openOuter()) {
            final Optional<FluidInformation> contained = get(stack);

            return contained.map(fluid -> {
                final FluidVariant variant = makeVariant(fluid);
                final ContainerItemContext containerContext = ContainerItemContext.withConstant(stack);

                FluidStorage.ITEM.find(stack, containerContext).extract(variant, amount, context);

                final StorageView<ItemVariant> itemVariant = containerContext.getMainSlot().iterator().next();
                return itemVariant.getResource().toStack((int) itemVariant.getAmount());
            }).orElse(ItemStack.EMPTY);
        }
    }

    @Override
    @SuppressWarnings("removal")
    public ItemStack insertInto(final ItemStack stack, final FluidInformation fluidInformation) {
        try (final Transaction context = Transaction.openOuter()) {
            final Optional<FluidInformation> contained = get(stack);

            return contained.map(fluid -> {
                final FluidVariant variant = makeVariant(fluid);
                final ContainerItemContext containerContext = ContainerItemContext.withConstant(stack);

                FluidStorage.ITEM.find(stack, containerContext).insert(variant, fluidInformation.amount(), context);

                final StorageView<ItemVariant> itemVariant = containerContext.getMainSlot().iterator().next();
                return itemVariant.getResource().toStack((int) itemVariant.getAmount());
            }).orElse(ItemStack.EMPTY);
        }
    }

    @Override
    public long simulateInsert(final ItemStack stack, final FluidInformation fluidInformation) {
        final FluidVariant variant = makeVariant(fluidInformation);
        final ContainerItemContext containerContext = ContainerItemContext.withInitial(stack);
        return FluidStorage.ITEM.find(stack, containerContext).simulateInsert(variant, fluidInformation.amount(), null);
    }

    @Override
    public long simulateExtract(final ItemStack stack, final long amount) {
        final Optional<FluidInformation> contained = get(stack);

        return contained.map(fluid -> {
            final FluidVariant variant = makeVariant(fluid);
            final ContainerItemContext containerContext = ContainerItemContext.withInitial(stack);

            return FluidStorage.ITEM.find(stack, containerContext).simulateExtract(variant, amount, null);
        }).orElse(0L);
    }

    @Override
    public Component getDisplayName(final Fluid fluid) {
        return fluid.defaultFluidState().createLegacyBlock().getBlock().getName();
    }

    @Override
    public String fluidStackTag() {
        return "Fluid";
    }

    @Override
    public Optional<IFluidVariantHandler> getVariantHandlerFor(Fluid fluid) {
        return Optional.of(new FabricFluidVariantHandlerDelegate(FluidVariantAttributes.getHandlerOrDefault(fluid)));
    }

    public static FluidVariant makeVariant(final FluidInformation fluid) {
        if (!fluid.fluid().isSource(fluid.fluid().defaultFluidState()) && fluid.fluid() != Fluids.EMPTY) {
            //We have a flowing fluid.
            //Let's make a none flowing variant of it.
            return makeVariant(fluid.withSource());
        }

        if (fluid.data() == null)
            return FluidVariant.of(fluid.fluid());

        return FluidVariant.of(fluid.fluid(), fluid.data());
    }

    public static FluidInformation makeInformation(final FluidVariant fluid, final long count) {
        if (!fluid.getFluid().isSource(fluid.getFluid().defaultFluidState()) && fluid.getFluid() != Fluids.EMPTY) {
            //We have a flowing fluid.
            //Let's make a none flowing variant of it.
            if (fluid.getFluid() instanceof FlowingFluid flowingFluid) {
                return makeInformation(FluidVariant.of(flowingFluid.getSource(), fluid.copyNbt()), count);
            }
        }

        if (fluid.copyNbt() == null)
            return new FluidInformation(fluid.getFluid(), count);

        return new FluidInformation(fluid.getFluid(), count, fluid.copyNbt());
    }

    public static FluidInformation makeInformation(final FluidVariant fluid) {
        return makeInformation(fluid, 1);
    }

    @Override
    public long getBucketAmount() {
        return FluidConstants.BUCKET;
    }
}
