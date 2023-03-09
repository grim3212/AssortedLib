package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.services.IFluidManager;
import com.grim3212.assorted.lib.proxy.LibFabricProxy;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

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
        final ContainerItemContext containerContext = ContainerItemContext.withConstant(stack);
        return FluidStorage.ITEM.find(stack, containerContext).simulateInsert(variant, fluidInformation.amount(), null);
    }

    @Override
    public long simulateExtract(final ItemStack stack, final long amount) {
        final Optional<FluidInformation> contained = get(stack);

        return contained.map(fluid -> {
            final FluidVariant variant = makeVariant(fluid);
            final ContainerItemContext containerContext = ContainerItemContext.withConstant(stack);

            return FluidStorage.ITEM.find(stack, containerContext).simulateExtract(variant, amount, null);
        }).orElse(0L);
    }

    @Override
    public int getFluidColor(final FluidInformation fluid) {
        return LibFabricProxy.INSTANCE.getFluidColor(fluid);
    }

    @Override
    public Component getDisplayName(final Fluid fluid) {
        return LibFabricProxy.INSTANCE.getFluidDisplayName(fluid);
    }

    @Override
    public String fluidStackTag() {
        return "Fluid";
    }

    public static FluidVariant makeVariant(final FluidInformation fluid) {
        if (fluid.data() == null)
            return FluidVariant.of(fluid.fluid());

        return FluidVariant.of(fluid.fluid(), fluid.data());
    }

    @Override
    public long getBucketAmount() {
        return FluidConstants.BUCKET;
    }
}
