package com.grim3212.assorted.lib.fluid;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.core.fluid.IFluidVariantHandler;
import com.grim3212.assorted.lib.platform.ForgeFluidManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Optional;

public class ForgeFluidVariantHandlerDelegate implements IFluidVariantHandler {
    private final FluidType delegate;

    public ForgeFluidVariantHandlerDelegate(final FluidType delegate) {
        this.delegate = delegate;
    }

    @Override
    public Component getName(final FluidInformation fluidInformation) {
        return delegate.getDescription(ForgeFluidManager.buildFluidStack(fluidInformation));
    }

    @Override
    public Optional<SoundEvent> getFillSound(final FluidInformation variant) {
        return Optional.ofNullable(delegate.getSound(ForgeFluidManager.buildFluidStack(variant), SoundActions.BUCKET_FILL));
    }

    @Override
    public Optional<SoundEvent> getEmptySound(final FluidInformation variant) {
        return Optional.ofNullable(delegate.getSound(ForgeFluidManager.buildFluidStack(variant), SoundActions.BUCKET_EMPTY));
    }

    @Override
    public int getLuminance(final FluidInformation variant) {
        return delegate.getLightLevel(ForgeFluidManager.buildFluidStack(variant));
    }

    @Override
    public int getTemperature(final FluidInformation variant) {
        return delegate.getTemperature(ForgeFluidManager.buildFluidStack(variant));
    }

    @Override
    public int getViscosity(final FluidInformation variant) {
        return delegate.getViscosity(ForgeFluidManager.buildFluidStack(variant));
    }

    @Override
    public int getDensity(final FluidInformation variant) {
        return delegate.getDensity(ForgeFluidManager.buildFluidStack(variant));
    }

    @Override
    public int getTintColor(final FluidInformation variant) {
        final FluidTintSource tintSource = fluidModel(variant).fluidTintSource();
        return tintSource == null ? -1 : tintSource.colorAsStack(ForgeFluidManager.buildFluidStack(variant));
    }

    @Override
    public Optional<Identifier> getStillTexture(final FluidInformation variant) {
        return Optional.of(fluidModel(variant).stillMaterial().sprite().contents().name());
    }

    @Override
    public Optional<Identifier> getFlowingTexture(final FluidInformation variant) {
        return Optional.of(fluidModel(variant).flowingMaterial().sprite().contents().name());
    }

    // TODO(26.2): the still/flowing textures and the tint colour no longer live on the FluidType.
    //  IClientFluidTypeExtensions#getStillTexture / #getFlowingTexture / #getTintColor are gone; a
    //  fluid's appearance is a baked FluidModel registered per Fluid through
    //  RegisterFluidModelsEvent, so it is only reachable through the client model manager and only
    //  after the models have been baked. The delegate therefore looks the model up by the fluid of
    //  the variant instead of asking the FluidType, and the extra fluid data (FluidInformation#data)
    //  can no longer influence the result the way a FluidStack aware extension could.
    private static FluidModel fluidModel(final FluidInformation variant) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(variant.fluid().defaultFluidState());
    }
}
