package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * {@code LazyOptional} is gone. Capabilities are plain nullable values now - a lookup through
 * {@code Capabilities.Item.BLOCK} either hands back a {@link ResourceHandler} or {@code null} - and
 * the "invalidate the token so every holder drops it" contract went with it. Cached handlers are
 * simply dropped here instead; a block entity that changes what it exposes tells the game itself
 * with {@code level.invalidateCapabilities(pos)}.
 */
public class ForgePlatformInventoryStorageHandlerSided implements IPlatformInventoryStorageHandler {

    private final Function<Direction, IItemStorageHandler> handler;
    private final Map<Direction, ResourceHandler<ItemResource>> itemHandlers;

    public ForgePlatformInventoryStorageHandlerSided(Function<Direction, IItemStorageHandler> handler) {
        this.handler = handler;
        this.itemHandlers = new HashMap<>();
    }

    @Nullable
    public ResourceHandler<ItemResource> getCapability(@Nullable Direction direction) {
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
