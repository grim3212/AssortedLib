package com.grim3212.assorted.lib.fluid;

import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
import com.grim3212.assorted.lib.platform.FabricFluidManager;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FabricFluidVariantRenderHandlerDelegate implements FluidVariantRenderHandler {
    private final IFluidVariantHandler delegate;

    public FabricFluidVariantRenderHandlerDelegate(final IFluidVariantHandler delegate) {
        this.delegate = delegate;
    }

    // TODO(26.2): the getSprites(FluidVariant) hook this class used to implement is gone from
    //  FluidVariantRenderHandler; only appendTooltip and getColor are left on it. A fluid's still and
    //  flowing textures are not supplied by code anymore - they come from a FluidModel.Unbaked
    //  registered against the fluid through
    //  net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry#register(Fluid,
    //  FluidModel.Unbaked[, FluidRenderHandler]), which is a different (and data shaped) API. The
    //  IFluidVariantHandler still/flowing texture pair is therefore only readable back out of this
    //  delegate, not out of a foreign handler; see FabricFluidVariantHandlerDelegate.

    @Override
    public int getColor(final FluidVariant fluidVariant, @Nullable final BlockAndTintGetter view, @Nullable final BlockPos pos) {
        return delegate.getTintColor(FabricFluidManager.makeInformation(fluidVariant));
    }

    public IFluidVariantHandler getDelegate() {
        return delegate;
    }
}
