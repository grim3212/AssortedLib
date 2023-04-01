package com.grim3212.assorted.lib.core.inventory;

public interface ISerializableItemStorageHandler extends IItemStorageHandler {
    void save();

    void load();
}
