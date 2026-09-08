package com.grim3212.assorted.lib.client.model;

import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.lib.mixin.client.AccessorBlockModel;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetexturableBlockModel extends BlockModel {
    private final Map<String, Material> replacements = new HashMap<>();

    public static RetexturableBlockModel from(BlockModel parent, Identifier parentLocation) {
        RetexturableBlockModel model = new RetexturableBlockModel(parentLocation, parent.getElements(), ((AccessorBlockModel) parent).assortedlib_getTextureMap(), parent.hasAmbientOcclusion(), parent.getGuiLight(), parent.getTransforms(), parent.getOverrides());
        return model;
    }

    public RetexturableBlockModel(Identifier parentLocation, List<BlockElement> elements, Map<String, Either<Material, String>> textures, boolean ambientOcclusion, GuiLight guiLight, ItemTransforms cameraTransforms, List<ItemOverride> overrides) {
        super(parentLocation, elements, textures, ambientOcclusion, guiLight, cameraTransforms, overrides);
    }

    @Override
    public Material getMaterial(String nameIn) {
        if (this.replacements.containsKey(nameIn)) {
            return this.replacements.get(nameIn);
        }

        return super.getMaterial(nameIn);
    }

    public void replaceTexture(String name, Identifier texture) {
        this.replacements.put(name, new Material(TextureAtlas.LOCATION_BLOCKS, texture));
    }

    public RetexturableBlockModel retexture(ImmutableMap<String, String> textures) {
        textures.forEach((name, texture) -> replaceTexture(name, Identifier.parse(texture)));
        return this;
    }
}
