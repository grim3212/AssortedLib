package com.grim3212.assorted.lib.client.render;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class ColorHandlers {

    /**
     * {@code BlockColor} became {@link BlockTintSource} in 26.2. It is still a runtime registration,
     * held by {@code BlockColors}, but a block now registers a whole list of tint layers rather than a
     * single handler; this consumer registers the given source as the block's only layer.
     */
    public interface BlockHandlerConsumer {
        void register(BlockTintSource handler, Block... blocks);
    }

    // TODO(26.2): item tinting is no longer registered per item. ItemColor / ItemColors are gone;
    //  an item's tints now live in its item model JSON as a list of ItemTintSource entries, and the
    //  only thing code registers is the MapCodec that deserialises a custom source type, keyed by
    //  Identifier into net.minecraft.client.color.item.ItemTintSources (whose id mapper is private, so
    //  each loader has to expose its own hook into it). That is what this consumer now takes. Callers
    //  that used to attach an ItemColor to a set of items must instead emit a "tints" entry referencing
    //  that id from the items' model JSON - there is no runtime equivalent.
    public interface ItemHandlerConsumer {
        void register(Identifier id, MapCodec<? extends ItemTintSource> handler);
    }
}
