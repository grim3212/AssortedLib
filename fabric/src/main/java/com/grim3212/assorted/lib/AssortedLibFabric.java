package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.platform.FabricConfigHelper;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.api.ModInitializer;

public class AssortedLibFabric implements ModInitializer {


    @Override
    public void onInitialize() {
        LibConstants.LOG.info(LibConstants.MOD_NAME + " starting up...");

        Services.CONDITIONS.init();
        Services.INGREDIENTS.register();

        FabricConfigHelper.init();
    }

}
