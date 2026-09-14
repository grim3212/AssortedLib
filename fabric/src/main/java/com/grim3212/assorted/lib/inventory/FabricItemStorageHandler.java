package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class FabricItemStorageHandler extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements SlottedStorage<ItemVariant> {

    private final IItemStorageHandler storage;

    public FabricItemStorageHandler(@NotNull IItemStorageHandler storage) {
        super(new SlotList(storage));
        this.storage = storage;
    }

    /**
     * Live view. {@link CombinedStorage} keeps the list it is constructed with, but a slot count can
     * change - a crate controller's grows as it finds crates on its first server tick.
     */
    private static final class SlotList extends AbstractList<SingleSlotStorage<ItemVariant>> {

        private final IItemStorageHandler storage;
        // Cached: each slot is a SnapshotParticipant, so a transaction must get the same object back.
        private final List<SingleSlotStorage<ItemVariant>> slots = new ArrayList<>();

        private SlotList(IItemStorageHandler storage) {
            this.storage = storage;
        }

        @Override
        public SingleSlotStorage<ItemVariant> get(int index) {
            while (this.slots.size() <= index) {
                this.slots.add(new FabricItemSlot(this.storage, this.slots.size()));
            }

            return this.slots.get(index);
        }

        @Override
        public int size() {
            return this.storage.getSlots();
        }
    }

    @Override
    public List<SingleSlotStorage<ItemVariant>> getSlots() {
        return this.parts;
    }

    @Override
    public int getSlotCount() {
        return this.parts.size();
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return this.parts.get(slot);
    }
}
