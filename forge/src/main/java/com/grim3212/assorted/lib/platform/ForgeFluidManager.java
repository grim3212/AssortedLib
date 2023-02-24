package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.services.IFluidManager;
import com.grim3212.assorted.lib.proxy.LibForgeProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ForgeFluidManager implements IFluidManager {

    @Override
    public Optional<FluidInformation> get(final ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                .map(handler -> handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE))
                .map(fluidStack -> new FluidInformation(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.isEmpty() ? new CompoundTag() : fluidStack.getOrCreateTag()));
    }

    @Override
    public ItemStack extractFrom(final ItemStack stack, final long amount) {
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> handler.drain((int) amount, IFluidHandler.FluidAction.EXECUTE));
        return stack;
    }

    @Override
    public long simulateExtract(ItemStack stack, long amount) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(handler -> handler.drain((int) amount, IFluidHandler.FluidAction.SIMULATE).getAmount()).orElse(0);
    }

    @Override
    public ItemStack insertInto(final ItemStack stack, final FluidInformation fluidInformation) {
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> handler.fill(buildFluidStack(fluidInformation), IFluidHandler.FluidAction.EXECUTE));
        return stack;
    }

    @Override
    public long simulateInsert(ItemStack stack, FluidInformation fluidInformation) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(handler -> handler.fill(buildFluidStack(fluidInformation), IFluidHandler.FluidAction.SIMULATE)).orElse(0);
    }

    @Override
    public int getFluidColor(final FluidInformation fluid) {
        return LibForgeProxy.INSTANCE.getFluidColor(fluid);
    }

    @Override
    public Component getDisplayName(final Fluid fluid) {
        return fluid.getFluidType().getDescription(buildFluidStack(new FluidInformation(fluid)));
    }

    @NotNull
    private FluidStack buildFluidStack(final FluidInformation fluid) {
        if (fluid.data() == null)
            return new FluidStack(fluid.fluid(), (int) fluid.amount());

        return new FluidStack(fluid.fluid(), (int) fluid.amount(), fluid.data());
    }
}
