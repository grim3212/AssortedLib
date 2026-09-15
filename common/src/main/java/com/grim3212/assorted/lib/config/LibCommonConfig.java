package com.grim3212.assorted.lib.config;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class LibCommonConfig {

    public final Supplier<Boolean> syncVanillaRecipesForManual;

    public LibCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, LibConstants.MOD_ID + "-common");

        syncVanillaRecipesForManual = builder.defineBoolean("manual.syncVanillaRecipes", true,
                "Send vanilla crafting, smelting and stonecutting recipes to clients so the instruction manual can draw them.");

        builder.setup();
    }
}
