package com.grim3212.assorted.lib.fluid;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
import com.grim3212.assorted.lib.dist.DistExecutor;
import com.grim3212.assorted.lib.platform.FabricFluidManager;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

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
        return delegate.getLightEmission(FabricFluidManager.makeVariant(variant));
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
    public Optional<Identifier> getStillTexture(final FluidInformation variant) {

        return DistExecutor.unsafeRunForDist(
                () -> () -> {
                    final FluidVariantRenderHandler handler = FluidVariantRendering.getHandlerOrDefault(variant.fluid());
                    if (handler instanceof FabricFluidVariantRenderHandlerDelegate renderDelegate) {
                        return renderDelegate.getDelegate().getStillTexture(variant);
                    }

                    return fluidModel(variant.fluid()).map(model -> model.stillMaterial().sprite().contents().name());
                },
                () -> Optional::empty
        );
    }

    @Override
    public Optional<Identifier> getFlowingTexture(final FluidInformation variant) {
        return DistExecutor.unsafeRunForDist(
                () -> () -> {
                    final FluidVariantRenderHandler handler = FluidVariantRendering.getHandlerOrDefault(variant.fluid());
                    if (handler instanceof FabricFluidVariantRenderHandlerDelegate renderDelegate) {
                        return renderDelegate.getDelegate().getFlowingTexture(variant);
                    }

                    return fluidModel(variant.fluid()).map(model -> model.flowingMaterial().sprite().contents().name());
                },
                () -> Optional::empty
        );
    }

    /**
     * The baked {@link FluidModel} the model manager holds for the fluid, which is the only place a
     * fluid's textures live: {@code FluidVariantRendering#getSprites} and
     * {@code FluidVariantRenderHandler#getSprites} are both gone. {@code FabricClientFluidHelper} and
     * NeoForge's {@code ForgeFluidVariantHandlerDelegate} read the same source, so every path answers
     * alike. Only readable once models have baked, and a {@link FluidInformation}'s extra data cannot
     * change the answer.
     */
    private static Optional<FluidModel> fluidModel(final Fluid fluid) {
        if (fluid == Fluids.EMPTY) {
            return Optional.empty();
        }

        return Optional.ofNullable(Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState()));
    }
}
