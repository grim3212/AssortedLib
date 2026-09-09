package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.services.IClientFluidHelper;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

/**
 * Fluid textures are no longer answered by the transfer API: {@code FluidVariantRendering#getSprite(s)}
 * is gone, and a fluid's still/flowing/overlay materials now live in a baked {@link FluidModel} that the
 * model manager holds per fluid state. Only the tint is still a variant level question.
 */
public class FabricClientFluidHelper implements IClientFluidHelper {

    @Override
    public int getFluidColor(FluidInformation fluid) {
        return FluidVariantRendering.getColor(FabricFluidManager.makeVariant(fluid));
    }

    @Override
    public TextureAtlasSprite getSprite(FluidInformation fluid) {
        return model(fluid.fluid()).stillMaterial().sprite();
    }

    @Override
    public Identifier getFlowingFluidTexture(final FluidInformation fluidInformation) {
        return getFlowingFluidTexture(fluidInformation.fluid());
    }

    @Override
    public Identifier getFlowingFluidTexture(final Fluid fluid) {
        return model(fluid).flowingMaterial().sprite().contents().name();
    }

    @Override
    public Identifier getStillFluidTexture(final FluidInformation fluidInformation) {
        return getStillFluidTexture(fluidInformation.fluid());
    }

    @Override
    public Identifier getStillFluidTexture(final Fluid fluid) {
        return model(fluid).stillMaterial().sprite().contents().name();
    }

    private static FluidModel model(final Fluid fluid) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState());
    }
}
