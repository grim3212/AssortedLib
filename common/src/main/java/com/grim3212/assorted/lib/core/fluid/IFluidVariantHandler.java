package com.grim3212.assorted.lib.core.fluid;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

public interface IFluidVariantHandler {
    /**
     * Return the name that should be used for the passed fluid variant.
     */
    Component getName(FluidInformation fluidInformation);

    /**
     * Return the sound corresponding to this fluid being filled, or none if no sound is available.
     */
    Optional<SoundEvent> getFillSound(FluidInformation variant);

    /**
     * Return the sound corresponding to this fluid being emptied, or none if no sound is available.
     */
    Optional<SoundEvent> getEmptySound(FluidInformation variant);

    /**
     * Return an integer in [0, 15]: the light level emitted by this fluid, or 0 if it doesn't naturally emit light.
     */
    int getLuminance(FluidInformation variant);

    /**
     * Return a non-negative integer, representing the temperature of this fluid in Kelvin.
     */
    int getTemperature(FluidInformation variant);

    /**
     * Return a positive integer, representing the viscosity of this fluid.
     * Fluids with lower viscosity generally flow faster than fluids with higher viscosity.
     */
    int getViscosity(FluidInformation variant);

    /** The fluid's density; 0 or less means it is lighter than air. */
    int getDensity(FluidInformation variant);

    /** The fluid's tint colour. */
    int getTintColor(FluidInformation variant);

    /**
     * The fluid's still texture, if it has one. Both loaders answer from the baked {@code FluidModel}
     * the model manager holds per fluid state, which is where a fluid's textures live now. So this is
     * client only, readable only once models have baked, and the component data a
     * {@link FluidInformation} carries does not change the answer.
     */
    Optional<Identifier> getStillTexture(FluidInformation variant);

    /** The fluid's flowing texture, if it has one; same source as {@link #getStillTexture}. */
    Optional<Identifier> getFlowingTexture(FluidInformation variant);
}
