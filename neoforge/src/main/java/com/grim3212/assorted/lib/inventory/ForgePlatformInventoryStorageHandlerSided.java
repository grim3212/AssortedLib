package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * TODO(26.2): {@code LazyOptional} is gone. Capabilities are plain nullable values now - a lookup
 * through {@code Capabilities.ItemHandler.BLOCK} either hands back a handler or {@code null} - and
 * the "invalidate the token so every holder drops it" contract went with it. Cached handlers are
 * simply dropped here instead; a block entity that changes what it exposes has to tell the game
 * itself with {@code level.invalidateCapabilities(pos)}.
 */
public class ForgePlatformInventoryStorageHandlerSided implements IPlatformInventoryStorageHandler {

    private final Function<Direction, IItemStorageHandler> handler;
    private final Map<Direction, IItemHandler> itemHandlers;

    public ForgePlatformInventoryStorageHandlerSided(Function<Direction, IItemStorageHandler> handler) {
        this.handler = handler;
        this.itemHandlers = new HashMap<>();
    }

    @Nullable
    public IItemHandler getCapability(@Nullable Direction direction) {
        if (!this.itemHandlers.containsKey(direction)) {
            IItemStorageHandler newHandler = this.handler.apply(direction);
            this.itemHandlers.put(direction, newHandler == null ? null : new ForgeItemStorageHandler(newHandler));
        }
        return this.itemHandlers.get(direction);
    }

    @Override
    public void invalidate() {
        this.itemHandlers.clear();
    }

    @Override
    public IItemStorageHandler getItemStorageHandler(@Nullable Direction direction) {
        return this.handler.apply(direction);
    }

}
