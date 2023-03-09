package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.config.ConfigBuilder;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    String getCommonTagPrefix();

    void openMenu(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter);

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    boolean isPhysicalClient();

    boolean isFakePlayer(Player player);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    <T> ILoaderRegistry<T> getRegistry(ResourceKey<? extends Registry<T>> key);

    @FunctionalInterface
    public interface ScreenFactory<T, S> {
        S create(T menu, Inventory inventory, Component title);
    }

    void setupCommonConfig(String modId, ConfigBuilder builder);

    void setupClientConfig(String modId, ConfigBuilder builder);
}
