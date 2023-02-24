package com.grim3212.assorted.lib.client;

import com.grim3212.assorted.lib.LibProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class LibClientProxy implements LibProxy {
    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
