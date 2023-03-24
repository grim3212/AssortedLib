package com.grim3212.assorted.lib.fluid;

import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
import com.grim3212.assorted.lib.platform.FabricFluidManager;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;

public class FabricFluidVariantRenderHandlerDelegate implements FluidVariantRenderHandler {
    private final IFluidVariantHandler delegate;

    public FabricFluidVariantRenderHandlerDelegate(final IFluidVariantHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public @Nullable TextureAtlasSprite[] getSprites(final FluidVariant fluidVariant) {
        return new TextureAtlasSprite[]{
                Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(delegate.getStillTexture(FabricFluidManager.makeInformation(fluidVariant)).orElseThrow()),
                delegate.getFlowingTexture(FabricFluidManager.makeInformation(fluidVariant)).map(texture -> Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture)).orElse(null)
        };
    }

    @Override
    public int getColor(final FluidVariant fluidVariant, @Nullable final BlockAndTintGetter view, @Nullable final BlockPos pos) {
        return delegate.getTintColor(FabricFluidManager.makeInformation(fluidVariant));
    }

    public IFluidVariantHandler getDelegate() {
        return delegate;
    }
}
