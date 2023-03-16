package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    boolean isFakePlayer(Player player);

    /**
     * The current distribution.
     *
     * @return The current distribution.
     */
    Dist getCurrentDistribution();

    boolean isPhysicalClient();

    /**
     * Indicates if the current platform is running in production mode or not.
     *
     * @return True if the current platform is running in production mode. False when not.
     */
    boolean isProduction();

    <T> ILoaderRegistry<T> getRegistry(ResourceKey<? extends Registry<T>> key);

    @FunctionalInterface
    public interface ScreenFactory<T, S> {
        S create(T menu, Inventory inventory, Component title);
    }

    void registerCreativeTab(ResourceLocation id, Component title, Supplier<ItemStack> icon, Supplier<List<ItemStack>> displayStacks);
}
