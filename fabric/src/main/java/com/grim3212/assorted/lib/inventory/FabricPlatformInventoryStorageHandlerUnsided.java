package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class FabricPlatformInventoryStorageHandlerUnsided implements IPlatformInventoryStorageHandler {

    private final IItemStorageHandler handler;
    private final InventoryStorage storage;


    public FabricPlatformInventoryStorageHandlerUnsided(IItemStorageHandler handler) {
        this.handler = handler;
        this.storage = new FabricItemStorageHandler(handler);
    }

    @Override
    public void invalidate() {
    }

    public InventoryStorage getFabricInventory() {
        return this.storage;
    }

    @Override
    public IItemStorageHandler getItemStorageHandler(@Nullable Direction direction) {
        return this.handler;
    }
}
