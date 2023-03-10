package com.grim3212.assorted.lib.client.events;

import com.grim3212.assorted.lib.client.render.ColorHandlers;
import com.grim3212.assorted.lib.events.GenericEvent;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;

public class RegisterItemColorEvent extends GenericEvent {

    private final ItemColors itemColors;
    private final ColorHandlers.ItemHandlerConsumer consumer;

    public RegisterItemColorEvent(ItemColors itemColors, ColorHandlers.ItemHandlerConsumer consumer) {
        this.itemColors = itemColors;
        this.consumer = consumer;
    }

    public ItemColors getItemColors() {
        return itemColors;
    }

    public void register(ItemColor color, Item... items) {
        this.consumer.register(color, items);
    }
}
