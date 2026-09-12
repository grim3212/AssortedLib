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

    // FluidVariantRenderHandler has no getSprites any more; a fluid's textures come from a
    // FluidModel.Unbaked registered through FluidRenderingRegistry. Only the colour is still a
    // variant level question, so that is all this delegate answers - the textures are read off the
    // baked model instead, by FabricFluidVariantHandlerDelegate, for our handlers and foreign ones
    // alike.

    @Override
    public int getColor(final FluidVariant fluidVariant, @Nullable final BlockAndTintGetter view, @Nullable final BlockPos pos) {
        return delegate.getTintColor(FabricFluidManager.makeInformation(fluidVariant));
    }

    public IFluidVariantHandler getDelegate() {
        return delegate;
    }
}
