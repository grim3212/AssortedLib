package com.grim3212.assorted.lib.core.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

/**
 * A menu provider whose client-side menu is built from data the server sends. Open it with
 * {@code Services.PLATFORM.openMenu}; the data is written with the codec its menu type was
 * created with, so the type's factory reads back exactly this value.
 *
 * @param <D> the data the client-side menu factory takes
 */
public interface IMenuDataProvider<D> extends MenuProvider {

    D getMenuData(ServerPlayer player);
}
