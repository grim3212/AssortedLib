package com.grim3212.assorted.lib.client.render;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;

// TODO(26.2): this is now only the id -> MapCodec hook for SpecialModelRenderer.Unbaked types,
//  which an item selects from data with "minecraft:special" (vanilla's id mapper is private, so
//  each loader exposes it). The IBEWLR name is kept to avoid import churn; rename it with the
//  client/model work.
public interface IBEWLR {
    /**
     * Registers a {@link SpecialModelRenderer.Unbaked} codec under {@code id}, which item models
     * name to select the renderer.
     */
    void registerSpecialModelRenderer(final Identifier id, final MapCodec<? extends SpecialModelRenderer.Unbaked<?>> renderer);
}
