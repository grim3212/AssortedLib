package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IClientHelper;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

public class FabricClientHelper implements IClientHelper {

    @Override
    public <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void registerScreen(MenuType<? extends T> menuType, IPlatformHelper.ScreenFactory<T, S> factory) {
        MenuScreens.register(menuType, factory::create);
    }

    @Override
    public void registerBEWLR(Item item, BlockEntityWithoutLevelRenderer renderer) {
        BuiltinItemRendererRegistry.INSTANCE.register(
                item,
                renderer::renderByItem
        );
    }

}
