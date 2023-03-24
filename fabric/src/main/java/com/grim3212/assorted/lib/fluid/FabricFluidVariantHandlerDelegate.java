package com.grim3212.assorted.lib.fluid;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
import com.grim3212.assorted.lib.dist.DistExecutor;
import com.grim3212.assorted.lib.platform.FabricFluidManager;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

public class FabricFluidVariantHandlerDelegate implements IFluidVariantHandler {
    private final FluidVariantAttributeHandler delegate;

    public FabricFluidVariantHandlerDelegate(final FluidVariantAttributeHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public Component getName(final FluidInformation fluidInformation) {
        return delegate.getName(FabricFluidManager.makeVariant(fluidInformation));
    }

    @Override
    public Optional<SoundEvent> getFillSound(final FluidInformation variant) {
        return delegate.getFillSound(FabricFluidManager.makeVariant(variant));
    }

    @Override
    public Optional<SoundEvent> getEmptySound(final FluidInformation variant) {
        return delegate.getEmptySound(FabricFluidManager.makeVariant(variant));
    }

    @Override
    public int getLuminance(final FluidInformation variant) {
        return delegate.getLuminance(FabricFluidManager.makeVariant(variant));
    }

    @Override
    public int getTemperature(final FluidInformation variant) {
        return delegate.getTemperature(FabricFluidManager.makeVariant(variant));
    }

    @Override
    public int getViscosity(final FluidInformation variant) {
        return delegate.getViscosity(FabricFluidManager.makeVariant(variant), null);
    }

    @Override
    public int getDensity(final FluidInformation variant) {
        if (delegate.isLighterThanAir(FabricFluidManager.makeVariant(variant))) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public int getTintColor(final FluidInformation variant) {
        return DistExecutor.unsafeRunForDist(
                () -> () -> FluidVariantRendering.getColor(FabricFluidManager.makeVariant(variant)),
                () -> () -> 0xffffff
        );
    }

    @Override
    public Optional<ResourceLocation> getStillTexture(final FluidInformation variant) {

        return DistExecutor.unsafeRunForDist(
                () -> () -> {
                    final FluidVariantRenderHandler handler = FluidVariantRendering.getHandlerOrDefault(variant.fluid());
                    if (handler instanceof FabricFluidVariantRenderHandlerDelegate renderDelegate) {
                        return renderDelegate.getDelegate().getStillTexture(variant);
                    }

                    return Optional.ofNullable(FluidVariantRendering.getSprites(FabricFluidManager.makeVariant(variant))).map(sprites -> sprites[0]).map(TextureAtlasSprite::contents).map(SpriteContents::name);
                },
                () -> Optional::empty
        );
    }

    @Override
    public Optional<ResourceLocation> getFlowingTexture(final FluidInformation variant) {
        return DistExecutor.unsafeRunForDist(
                () -> () -> {
                    final FluidVariantRenderHandler handler = FluidVariantRendering.getHandlerOrDefault(variant.fluid());
                    if (handler instanceof FabricFluidVariantRenderHandlerDelegate renderDelegate) {
                        return renderDelegate.getDelegate().getFlowingTexture(variant);
                    }

                    return Optional.ofNullable(FluidVariantRendering.getSprites(FabricFluidManager.makeVariant(variant))).map(sprites -> sprites[1]).map(TextureAtlasSprite::contents).map(SpriteContents::name);
                },
                () -> Optional::empty
        );
    }
}
