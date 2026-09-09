package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
import com.grim3212.assorted.lib.fluid.ForgeFluidVariantHandlerDelegate;
import com.grim3212.assorted.lib.platform.services.IFluidManager;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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

    // TODO(26.2): nothing identifies an item's stored fluid by name any more - it lives in a
    //  SimpleFluidContent data component whose type the holder picks - so this has no meaningful
    //  answer on either loader and IFluidManager#fluidStackTag should be dropped from the common
    //  interface. The Fabric side returns the same string, so the two still agree until it is.
    @Override
    public String fluidStackTag() {
        return "Fluid";
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

    @NotNull
    public static FluidInformation buildFluidInformation(final FluidStack fluid) {
        if (fluid.isComponentsPatchEmpty())
            return new FluidInformation(fluid.getFluid(), fluid.getAmount());

        return new FluidInformation(fluid.getFluid(), fluid.getAmount(), toTag(fluid.getComponentsPatch()));
    }

    @NotNull
    public static FluidStack buildFluidStack(final FluidInformation fluid) {
        if (fluid.data() == null || fluid.data().isEmpty())
            return new FluidStack(fluid.fluid(), clamp(fluid.amount()));

        return new FluidStack(fluid.fluid(), clamp(fluid.amount()), fromTag(fluid.data()));
    }

    // TODO(26.2): FluidInformation still models a fluid's extra data as a CompoundTag, which is the
    //  1.20.1 shape; a FluidStack carries a DataComponentPatch. Converting between the two here is
    //  the only reason these two methods exist, and it is lossy - plain NbtOps is used because there
    //  is no registry access at these call sites, so a component that needs a registry to serialise
    //  is dropped. FluidInformation#data should become a DataComponentPatch on the common side, at
    //  which point both conversions go away.
    private static CompoundTag toTag(final DataComponentPatch patch) {
        return DataComponentPatch.CODEC.encodeStart(NbtOps.INSTANCE, patch)
                .result()
                .filter(tag -> tag instanceof CompoundTag)
                .map(tag -> (CompoundTag) tag)
                .orElseGet(CompoundTag::new);
    }

    private static DataComponentPatch fromTag(final Tag tag) {
        return DataComponentPatch.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(DataComponentPatch.EMPTY);
    }
}
