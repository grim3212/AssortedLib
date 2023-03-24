package com.grim3212.assorted.lib.client.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface IBlockModelAccessor {

    List<BlockElement> elements();

    BlockModel.GuiLight guiLight();

    boolean usesAmbientOcclusion();

    ItemTransforms transforms();

    List<ItemOverride> overrides();

    String name();

    Map<String, Either<Material, String>> textureMap();

    BlockModel parent();

    ResourceLocation parentLocation();

    ItemOverrides overrides(ModelBaker modelBaker, BlockModel model);
}
