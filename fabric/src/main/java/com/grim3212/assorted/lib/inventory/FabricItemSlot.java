package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

public class FabricItemSlot extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {

    private final IItemStorageHandler storageHandler;
    private final int slot;

    public FabricItemSlot(IItemStorageHandler storage, int slot) {
        this.storageHandler = storage;
        this.slot = slot;
    }

    public ItemStack getStack() {
        return this.storageHandler.getStackInSlot(slot);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank()) {
            return 0;
        }

        ItemStack stackToInsert = resource.toStack((int) maxAmount);
        ItemStack inserted = this.storageHandler.insertItem(this.slot, stackToInsert, true);
        // Were we able to add anything to the inventory
        if (inserted.isEmpty() || inserted.getCount() < stackToInsert.getCount()) {
            updateSnapshots(transaction);
            this.storageHandler.insertItem(this.slot, stackToInsert, false);
            return inserted.isEmpty() ? maxAmount : maxAmount - inserted.getCount();
        }
        return 0;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (!Services.INVENTORY.canItemStacksStack(this.getStack(), resource.toStack()))
            return 0;

        ItemStack extracted = this.storageHandler.extractItem(this.slot, (int) maxAmount, true);
        if (!extracted.isEmpty()) {
            updateSnapshots(transaction);
            this.storageHandler.extractItem(this.slot, (int) maxAmount, false);
            return extracted.getCount();
        }
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return this.getStack().isEmpty();
    }

    @Override
    public ItemVariant getResource() {
        return ItemVariant.of(this.getStack());
    }

    @Override
    public long getAmount() {
        return this.getStack().getCount();
    }

    @Override
    public long getCapacity() {
        return this.storageHandler.getSlotLimit(this.slot);
    }

    @Override
    protected ItemStack createSnapshot() {
        return this.getStack().copy();
    }

    @Override
    protected void readSnapshot(ItemStack snapshot) {
        this.storageHandler.setStackInSlot(this.slot, snapshot);
    }

}
