package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FabricPlatformInventoryStorageHandlerSided implements IPlatformInventoryStorageHandler {

    private final Function<Direction, IItemStorageHandler> handler;
    private final Map<Direction, InventoryStorage> inventoryStorageMap;


    public FabricPlatformInventoryStorageHandlerSided(Function<Direction, IItemStorageHandler> handler) {
        this.handler = handler;
        this.inventoryStorageMap = new HashMap<>();
    }

    @Override
    public void invalidate() {
    }

    public InventoryStorage getFabricInventory(@Nullable Direction direction) {
        if (!this.inventoryStorageMap.containsKey(direction)) {
            IItemStorageHandler newHandler = this.handler.apply(direction);
            if (newHandler == null) {
                this.inventoryStorageMap.put(direction, null);
            } else {
                this.inventoryStorageMap.put(direction, new FabricItemStorageHandler(newHandler));
            }
        }
        return this.inventoryStorageMap.get(direction);
    }

    @Override
    public IItemStorageHandler getItemStorageHandler(@Nullable Direction direction) {
        return this.handler.apply(direction);
    }
}
