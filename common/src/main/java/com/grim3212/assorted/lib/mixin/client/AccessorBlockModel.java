package com.grim3212.assorted.lib.mixin.client;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockModel.class)
public interface AccessorBlockModel {

    @Accessor("parentLocation")
    Identifier assortedlib_getParentLocation();

    @Accessor("textureMap")
    Map<String, Either<Material, String>> assortedlib_getTextureMap();

    @Accessor("GSON")
    static Gson assortedlib_getGson() {
        throw new AssertionError();
    }
}
