package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IClientHelper;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ForgeClientHelper implements IClientHelper {

    @Override
    public <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void registerScreen(MenuType<? extends T> menuType, IPlatformHelper.ScreenFactory<T, S> factory) {
        MenuScreens.register(menuType, factory::create);
    }
}
