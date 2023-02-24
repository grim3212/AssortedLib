package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.client.LibClientProxy;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface LibProxy {

    LibProxy INSTANCE = make();

    private static LibProxy make() {
        if (Services.PLATFORM.isPhysicalClient()) {
            return new LibClientProxy();
        } else {
            return new LibProxy() {
            };
        }
    }

    @Nullable
    default Player getClientPlayer() {
        return null;
    }
}
