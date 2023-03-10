package com.grim3212.assorted.lib.client.events;

import com.grim3212.assorted.lib.client.render.ColorHandlers;
import com.grim3212.assorted.lib.events.GenericEvent;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;

public class RegisterBlockColorEvent extends GenericEvent {

    private final BlockColors blockColors;
    private final ColorHandlers.BlockHandlerConsumer consumer;

    public RegisterBlockColorEvent(BlockColors blockColors, ColorHandlers.BlockHandlerConsumer consumer) {
        this.blockColors = blockColors;
        this.consumer = consumer;
    }

    public BlockColors getBlockColors() {
        return blockColors;
    }

    public void register(BlockColor color, Block... blocks) {
        this.consumer.register(color, blocks);
    }
}
