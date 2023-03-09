package com.grim3212.assorted.lib.platform;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.platform.services.IClientHelper;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.Optional;

public class ForgeClientHelper implements IClientHelper {

    private static final Map<Item, BlockEntityWithoutLevelRenderer> bewlrs = Maps.newConcurrentMap();

    @Override
    public <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void registerScreen(MenuType<? extends T> menuType, IPlatformHelper.ScreenFactory<T, S> factory) {
        MenuScreens.register(menuType, factory::create);
    }

    @Override
    public void registerBEWLR(Item item, BlockEntityWithoutLevelRenderer renderer) {
        bewlrs.put(item, renderer);
    }

    public static Optional<BlockEntityWithoutLevelRenderer> getRenderer(Item item) {
        return Optional.ofNullable(bewlrs.get(item));
    }
}
