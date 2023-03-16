package com.grim3212.assorted.lib.client.events;

import net.minecraft.client.Minecraft;

@FunctionalInterface
public interface ClientTickHandler {
    void handle(Minecraft client);
}
