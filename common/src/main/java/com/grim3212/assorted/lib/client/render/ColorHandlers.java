package com.grim3212.assorted.lib.client.render;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class ColorHandlers {

    /**
     * Registers the source as each block's only tint layer; {@code BlockColors} holds a list of
     * {@link BlockTintSource} layers per block.
     */
    public interface BlockHandlerConsumer {
        void register(BlockTintSource handler, Block... blocks);
    }

    // TODO(26.2): item tints are not registered per item any more: they live in the item model json
    //  as ItemTintSource entries, and code only registers a source type's MapCodec by id, which
    //  this consumer takes. Callers must add a "tints" entry naming that id to the items' model
    //  json.
    public interface ItemHandlerConsumer {
        void register(Identifier id, MapCodec<? extends ItemTintSource> handler);
    }
}
