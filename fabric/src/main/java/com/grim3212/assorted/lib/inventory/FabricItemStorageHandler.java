package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FabricItemStorageHandler extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements InventoryStorage {

    private final IItemStorageHandler storage;

    public FabricItemStorageHandler(@NotNull IItemStorageHandler storage) {
        super(getItemSlotsFromStorage(storage));
        this.storage = storage;
    }

    public static List<SingleSlotStorage<ItemVariant>> getItemSlotsFromStorage(@NotNull IItemStorageHandler storage) {
        List<SingleSlotStorage<ItemVariant>> slots = new ArrayList<>();

        for (int i = 0; i < storage.getSlots(); i++) {
            slots.add(new FabricItemSlot(storage, i));
        }

        return slots;
    }

    @Override
    public List<SingleSlotStorage<ItemVariant>> getSlots() {
        return this.parts;
    }
}
