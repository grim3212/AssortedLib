package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.client.model.FabricUnbakedModelTracker;
import com.grim3212.assorted.lib.client.model.block.LibBlockStateModels;
import com.grim3212.assorted.lib.client.model.loader.FabricSpecificationBlockStateModel;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
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

        // Must match the id ForgeSpecificationBlockStateModel is registered under: the blockstate
        // jsons naming it are generated once and shipped to both loaders.
        CustomUnbakedBlockStateModel.register(LibBlockStateModels.SPECIFICATION, FabricSpecificationBlockStateModel.MAP_CODEC);
    }
}
