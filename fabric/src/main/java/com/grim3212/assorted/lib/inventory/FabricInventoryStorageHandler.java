package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;

public class FabricInventoryStorageHandler implements IInventoryStorageHandler {

    private final IItemStorageHandler handler;
    private final InventoryStorage storage;


    public FabricInventoryStorageHandler(IItemStorageHandler handler) {
        this.handler = handler;
        this.storage = new FabricItemStorageHandler(handler);
    }

    @Override
    public void invalidate() {
    }

    @Override
    public IItemStorageHandler getItemStorageHandler() {
        return this.handler;
    }
}
