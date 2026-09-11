package com.grim3212.assorted.lib.core.inventory;

import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The factory and codec of every menu type made by {@code IPlatformHelper#createMenuType}. The menu
 * type is the only source of its codec, so the server's write and the client's read cannot drift.
 */
public final class MenuData {

    private static final Map<MenuType<?>, Entry<?, ?>> TYPES = new ConcurrentHashMap<>();

    private MenuData() {
    }

    /** Called by each loader's {@code createMenuType}. */
    public static <T extends AbstractContainerMenu, D> void register(MenuType<T> type, IPlatformHelper.MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        TYPES.put(type, new Entry<>(factory, codec));
    }

    public static boolean hasData(MenuType<?> type) {
        return TYPES.containsKey(type);
    }

    /** Writes a menu's data with its type's codec, as the server does when it opens the menu. */
    @SuppressWarnings("unchecked")
    public static void write(MenuType<?> type, Object data, RegistryFriendlyByteBuf buf) {
        ((Entry<?, Object>) entry(type)).codec().encode(buf, data);
    }

    /** Builds the client-side menu from the bytes {@link #write} produced. */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractContainerMenu> T read(MenuType<T> type, int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        return ((Entry<T, ?>) entry(type)).create(containerId, inventory, buf);
    }

    private static Entry<?, ?> entry(MenuType<?> type) {
        Entry<?, ?> entry = TYPES.get(type);
        if (entry == null) {
            throw new IllegalArgumentException("Menu type " + type + " carries no data; it was not made by createMenuType with a codec");
        }
        return entry;
    }

    private record Entry<T extends AbstractContainerMenu, D>(IPlatformHelper.MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        T create(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
            return this.factory.create(containerId, inventory, this.codec.decode(buf));
        }
    }
}
