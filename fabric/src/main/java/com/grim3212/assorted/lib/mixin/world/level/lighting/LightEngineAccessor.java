package com.grim3212.assorted.lib.mixin.world.level.lighting;

import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LightEngine.class)
public interface LightEngineAccessor<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>> {

    @Accessor
    LightChunkGetter getChunkSource();

    @Accessor
    S getStorage();

    @Invoker
    void callEnqueueIncrease(long l, long m);
}
