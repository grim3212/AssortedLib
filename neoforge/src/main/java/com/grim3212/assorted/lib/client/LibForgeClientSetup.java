package com.grim3212.assorted.lib.client;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.client.model.block.LibBlockStateModels;
import com.grim3212.assorted.lib.client.model.loader.ForgeSpecificationBlockStateModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;

/**
 * The library's own client side registrations (per-mod ones go through
 * {@code ForgeClientHelper.Registrations}). A second {@code @Mod} with {@code dist = Dist.CLIENT},
 * so client-only types like {@link RegisterBlockStateModels} never load on a dedicated server.
 */
@Mod(value = LibConstants.MOD_ID, dist = Dist.CLIENT)
public class LibForgeClientSetup {

    public LibForgeClientSetup(final IEventBus modBus, final ModContainer modContainer) {
        modBus.addListener(RegisterBlockStateModels.class, LibForgeClientSetup::registerBlockStateModels);
    }

    private static void registerBlockStateModels(final RegisterBlockStateModels event) {
        event.registerModel(LibBlockStateModels.SPECIFICATION, ForgeSpecificationBlockStateModel.MAP_CODEC);
    }
}
