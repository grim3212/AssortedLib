package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.client.model.FabricUnbakedModelTracker;
import com.grim3212.assorted.lib.platform.FabricNetworkHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class AssortedLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            FabricNetworkHelper.initializeClientHandlers();
        });

        FabricUnbakedModelTracker.register();
    }
}
