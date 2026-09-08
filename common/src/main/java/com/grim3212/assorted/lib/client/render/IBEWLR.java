package com.grim3212.assorted.lib.client.render;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;

// TODO(26.2): BlockEntityWithoutLevelRenderer is gone, and with it the ability to attach a special
//  renderer to an Item at runtime. Its replacement is SpecialModelRenderer<T>, produced by a
//  SpecialModelRenderer.Unbaked<T> that is looked up by Identifier through
//  net.minecraft.client.renderer.special.SpecialModelRenderers. An item opts in from data, by pointing
//  its item model at "minecraft:special" with that id, so the only registration left in code is the id
//  -> MapCodec pair below. SpecialModelRenderers' id mapper is private, so each loader must expose its
//  own hook into it (this interface is the common-side shape of that hook). The IBEWLR name is kept
//  only to avoid churning every import in one go; it should be renamed alongside the client/model work.
public interface IBEWLR {
    /**
     * Registers a new {@link SpecialModelRenderer.Unbaked} type so that item models may refer to it by id.
     *
     * @param id       The id item models use to select this renderer.
     * @param renderer The codec that deserialises the unbaked renderer.
     */
    void registerSpecialModelRenderer(final Identifier id, final MapCodec<? extends SpecialModelRenderer.Unbaked<?>> renderer);
}
