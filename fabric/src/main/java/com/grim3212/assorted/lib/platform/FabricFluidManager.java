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
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FabricFluidManager implements IFluidManager {
    @Override
    public Optional<FluidInformation> get(final ItemStack stack) {
        if (stack.isEmpty())
            return Optional.empty();

        // Reading only, so the constant context is enough here.
        final Storage<FluidVariant> target = FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
        if (target == null)
            return Optional.empty();

        if (!target.iterator().hasNext())
            return Optional.empty();

        final StorageView<FluidVariant> view = target.iterator().next();

        return Optional.of(makeInformation(view.getResource(), view.getAmount()));
    }

    @Override
    public ItemStack extractFrom(final ItemStack stack, final long amount) {
        final SingleItem item = new SingleItem(stack);
        final Storage<FluidVariant> fluids = item.fluids();
        if (fluids != null && amount > 0) {
            try (Transaction transaction = Transaction.openOuter()) {
                final ResourceAmount<FluidVariant> extracted = StorageUtil.extractAny(fluids, amount, transaction);
                if (extracted != null && extracted.amount() > 0) {
                    transaction.commit();
                }
            }
        }

        return item.contents();
    }

    @Override
    public long simulateExtract(final ItemStack stack, final long amount) {
        final Storage<FluidVariant> fluids = new SingleItem(stack).fluids();
        if (fluids == null || amount <= 0)
            return 0;

        // Closing the transaction without committing it rolls the extraction back.
        try (Transaction transaction = Transaction.openOuter()) {
            final ResourceAmount<FluidVariant> extracted = StorageUtil.extractAny(fluids, amount, transaction);
            return extracted == null ? 0 : extracted.amount();
        }
    }

    @Override
    public ItemStack insertInto(final ItemStack stack, final FluidInformation fluidInformation) {
        final SingleItem item = new SingleItem(stack);
        final Storage<FluidVariant> fluids = item.fluids();
        final FluidVariant variant = makeVariant(fluidInformation);
        if (fluids != null && !variant.isBlank() && fluidInformation.amount() > 0) {
            try (Transaction transaction = Transaction.openOuter()) {
                if (fluids.insert(variant, fluidInformation.amount(), transaction) > 0) {
                    transaction.commit();
                }
            }
        }

        return item.contents();
    }

    @Override
    public long simulateInsert(final ItemStack stack, final FluidInformation fluidInformation) {
        final Storage<FluidVariant> fluids = new SingleItem(stack).fluids();
        final FluidVariant variant = makeVariant(fluidInformation);
        if (fluids == null || variant.isBlank() || fluidInformation.amount() <= 0)
            return 0;

        try (Transaction transaction = Transaction.openOuter()) {
            return fluids.insert(variant, fluidInformation.amount(), transaction);
        }
    }

    @Override
    public Component getDisplayName(final Fluid fluid) {
        return fluid.defaultFluidState().createLegacyBlock().getBlock().getName();
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

        // TODO(26.2): FluidInformation still carries a CompoundTag, but a FluidVariant is keyed by a
        //  DataComponentPatch now - TransferVariant#copyNbt was replaced by #getComponentsPatch - and
        //  there is no conversion between the two that can be done here: a patch is only readable
        //  through DataComponentPatch.CODEC against a registry aware DynamicOps, which this static
        //  helper has no access to. The extra data is dropped in both directions until
        //  FluidInformation itself moves over to components in common.
        return FluidVariant.of(fluid.fluid());
    }

    public static FluidInformation makeInformation(final FluidVariant fluid, final long count) {
        if (!fluid.getFluid().isSource(fluid.getFluid().defaultFluidState()) && fluid.getFluid() != Fluids.EMPTY) {
            //We have a flowing fluid.
            //Let's make a none flowing variant of it.
            if (fluid.getFluid() instanceof FlowingFluid flowingFluid) {
                return makeInformation(FluidVariant.of(flowingFluid.getSource(), fluid.getComponentsPatch()), count);
            }
        }

        // See makeVariant: the variant's component patch has no CompoundTag to hand back.
        return new FluidInformation(fluid.getFluid(), count);
    }

    public static FluidInformation makeInformation(final FluidVariant fluid) {
        return makeInformation(fluid, 1);
    }

    @Override
    public long getBucketAmount() {
        return FluidConstants.BUCKET;
    }

    /**
     * One item out of the stack, in a slot of its own that a fluid storage can write to.
     * <p>
     * {@code ContainerItemContext.withConstant} is read only by contract, so a water bucket emptied
     * through it stays a water bucket. Transfers go through this slot instead: the item's storage
     * swaps what the slot holds (a water bucket for an empty one) inside the transaction, and the
     * slot is read back afterwards. It is a {@link SingleStackStorage}, so an aborted transaction
     * restores it.
     */
    private static final class SingleItem extends SingleStackStorage {
        private ItemStack stack;

        private SingleItem(final ItemStack stack) {
            this.stack = stack.copyWithCount(1);
        }

        @Override
        protected ItemStack getStack() {
            return this.stack;
        }

        @Override
        protected void setStack(final ItemStack stack) {
            this.stack = stack;
        }

        @Nullable
        private Storage<FluidVariant> fluids() {
            return this.stack.isEmpty() ? null : FluidStorage.ITEM.find(this.stack, ContainerItemContext.ofSingleSlot(this));
        }

        private ItemStack contents() {
            return this.stack;
        }
    }
}
