package com.grim3212.assorted.lib.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@FunctionalInterface
public interface LibScreenFactory<T, S> {
    S create(T menu, Inventory inventory, Component title);
}