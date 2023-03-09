package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.config.ConfigBuilder;
import com.grim3212.assorted.lib.config.FabricConfigUtil;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Consumer;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public String getCommonTagPrefix() {
        return "c";
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter) {
        player.openMenu(new ExtendedScreenHandlerImpl(provider, extraDataWriter));
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isFakePlayer(Player player) {
        return false;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> ILoaderRegistry<T> getRegistry(ResourceKey<? extends Registry<T>> key) {
        return FabricRegistryWrapper.getRegistry(key);
    }

    @Override
    public void setupCommonConfig(String modId, ConfigBuilder builder) {
        FabricConfigUtil.setupCommon(modId, builder);
    }

    @Override
    public void setupClientConfig(String modId, ConfigBuilder builder) {
        FabricConfigUtil.setupClient(modId, builder);
    }

    public static class ExtendedScreenHandlerImpl implements ExtendedScreenHandlerFactory {
        private final MenuProvider provider;
        private final Consumer<FriendlyByteBuf> extraDataWriter;

        public ExtendedScreenHandlerImpl(MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter) {
            this.provider = provider;
            this.extraDataWriter = extraDataWriter;
        }

        @Override
        public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
            extraDataWriter.accept(buf);
        }

        @Override
        public Component getDisplayName() {
            return provider.getDisplayName();
        }

        @Override
        public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
            return provider.createMenu(windowId, inventory, player);
        }
    }
}