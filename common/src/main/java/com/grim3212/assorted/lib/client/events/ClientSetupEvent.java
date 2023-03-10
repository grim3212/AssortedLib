package com.grim3212.assorted.lib.client.events;

import com.grim3212.assorted.lib.events.GenericEvent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;

public class ClientSetupEvent extends GenericEvent {

    private final TriConsumer<Item, ResourceLocation, ClampedItemPropertyFunction> itemPropertyConsumer;
    private final BiConsumer<Block, RenderType> renderTypeConsumer;

    public ClientSetupEvent(TriConsumer<Item, ResourceLocation, ClampedItemPropertyFunction> itemPropertyConsumer, BiConsumer<Block, RenderType> renderTypeConsumer) {
        this.itemPropertyConsumer = itemPropertyConsumer;
        this.renderTypeConsumer = renderTypeConsumer;
    }

    public void registerItemProperty(Item item, ResourceLocation location, ClampedItemPropertyFunction itemPropertyFunction) {
        this.itemPropertyConsumer.accept(item, location, itemPropertyFunction);
    }

    public void registerRenderType(Block block, RenderType renderType) {
        this.renderTypeConsumer.accept(block, renderType);
    }
}
