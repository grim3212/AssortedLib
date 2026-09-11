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

    // TODO(26.2): FluidVariantRenderHandler has no getSprites any more; textures come from a
    //  FluidModel.Unbaked registered through FluidRenderingRegistry. So the still/flowing textures
    //  can only be read back from this delegate, not a foreign handler (see
    //  FabricFluidVariantHandlerDelegate).

    @Override
    public int getColor(final FluidVariant fluidVariant, @Nullable final BlockAndTintGetter view, @Nullable final BlockPos pos) {
        return delegate.getTintColor(FabricFluidManager.makeInformation(fluidVariant));
    }

    public IFluidVariantHandler getDelegate() {
        return delegate;
    }
}
