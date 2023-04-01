package com.grim3212.assorted.lib.core.inventory;

public interface IInventoryStorageHandler {
    void invalidate();

    IItemStorageHandler getItemStorageHandler();
}
