package com.grim3212.assorted.lib.platform.services;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

public interface IClientHelper {

    <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void registerScreen(MenuType<? extends T> menuType, IPlatformHelper.ScreenFactory<T, S> factory);

    void registerBEWLR(final Item item, final BlockEntityWithoutLevelRenderer renderer);
}
