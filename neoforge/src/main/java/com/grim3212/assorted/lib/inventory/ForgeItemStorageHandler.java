package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Exposes an {@link IItemStorageHandler} as NeoForge's {@link ResourceHandler}, the type behind
 * {@code Capabilities.Item.*}. Rollback snapshots the whole slot list per
 * {@link TransactionContext} through a {@link SnapshotJournal}; the wrapped inventories are a few
 * slots, so simplicity wins over tracking single slots.
 */
public class ForgeItemStorageHandler implements ResourceHandler<ItemResource> {

    private final IItemStorageHandler storage;
    private final SlotJournal journal = new SlotJournal();

    public ForgeItemStorageHandler(@NotNull IItemStorageHandler storage) {
        this.storage = storage;
    }

    public IItemStorageHandler getStorage() {
        return storage;
    }

    @Override
    public int size() {
        return this.storage.getSlots();
    }

    @Override
    public ItemResource getResource(int index) {
        return ItemResource.of(this.storage.getStackInSlot(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        return this.storage.getStackInSlot(index).getCount();
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return this.storage.getSlotLimit(index);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return this.storage.isItemValid(index, resource.toStack(1));
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }

        this.journal.updateSnapshots(transaction);
        ItemStack remainder = this.storage.insertItem(index, resource.toStack(amount), false);
        return amount - remainder.getCount();
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0 || !resource.matches(this.storage.getStackInSlot(index))) {
            return 0;
        }

        this.journal.updateSnapshots(transaction);
        return this.storage.extractItem(index, amount, false).getCount();
    }

    /**
     * Captures every slot on the first mutation in a transaction and restores them on rollback, via
     * {@link IItemStorageHandler#captureSlot(int)} so nothing wider than an ItemStack is lost.
     */
    private final class SlotJournal extends SnapshotJournal<List<Runnable>> {

        @Override
        protected List<Runnable> createSnapshot() {
            List<Runnable> snapshot = new ArrayList<>(storage.getSlots());
            for (int slot = 0; slot < storage.getSlots(); slot++) {
                snapshot.add(storage.captureSlot(slot));
            }
            return snapshot;
        }

        @Override
        protected void revertToSnapshot(List<Runnable> snapshot) {
            snapshot.forEach(Runnable::run);
        }
    }
}
