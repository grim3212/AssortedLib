package com.grim3212.assorted.lib.client;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.client.manual.ManualClient;
import com.grim3212.assorted.lib.client.model.block.LibBlockStateModels;
import com.grim3212.assorted.lib.client.model.loader.ForgeSpecificationBlockStateModel;
import com.grim3212.assorted.lib.crafting.SyncedRecipes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.common.NeoForge;

/**
 * The library's own client side registrations (per-mod ones go through
 * {@code ForgeClientHelper.Registrations}). A second {@code @Mod} with {@code dist = Dist.CLIENT},
 * so client-only types like {@link RegisterBlockStateModels} never load on a dedicated server.
 */
@Mod(value = LibConstants.MOD_ID, dist = Dist.CLIENT)
public class LibForgeClientSetup {

    public LibForgeClientSetup(final IEventBus modBus, final ModContainer modContainer) {
        ManualClient.init();

        modBus.addListener(RegisterBlockStateModels.class, LibForgeClientSetup::registerBlockStateModels);

        // HIGHEST so the cache is filled before anything that starts from this same event reads it
        // - JEI waits on the recipe sync to load its plugins.
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, (final RecipesReceivedEvent event) -> {
            SyncedRecipes.set(event.getRecipeMap());
            // Sent on join and again on every /reload, which is where a condition can start reading
            // differently.
            ManualClient.refreshConditions();
        });
        NeoForge.EVENT_BUS.addListener((final ClientPlayerNetworkEvent.LoggingOut event) -> SyncedRecipes.clear());
    }

    private static void registerBlockStateModels(final RegisterBlockStateModels event) {
        event.registerModel(LibBlockStateModels.SPECIFICATION, ForgeSpecificationBlockStateModel.MAP_CODEC);
    }
}
