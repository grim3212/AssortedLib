package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class FabricWrappedItemHandler implements IItemStorageHandler {

    private final InventoryStorage storage;
    private final BlockEntity entity;

    public FabricWrappedItemHandler(BlockEntity entity, @NotNull InventoryStorage storage) {
        this.storage = storage;
        this.entity = entity;
    }

    @Override
    public int getSlots() {
        return this.storage.getSlots().size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        SingleSlotStorage<ItemVariant> slotStorage = this.storage.getSlot(slot);
        return slotStorage.getResource().toStack((int) slotStorage.getAmount());
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        SingleSlotStorage<ItemVariant> slotStorage = this.storage.getSlot(slot);
        if (!slotStorage.supportsInsertion() || stack.isEmpty()) {
            return stack;
        }

        if (!slotStorage.isResourceBlank() && !Services.INVENTORY.canItemStacksStack(stack, slotStorage.getResource().toStack((int) slotStorage.getAmount()))) {
            return stack;
        }

        ItemVariant toInsert = ItemVariant.of(stack);
        if (simulate) {
            int inserted = (int) slotStorage.simulateInsert(toInsert, stack.getCount(), null);
            if (inserted <= 0) {
                return stack;
            } else {
                int remainder = stack.getCount() - inserted;
                if (remainder > 0) {
                    return stack.copyWithCount(remainder);
                }
                return ItemStack.EMPTY;
            }
        }

        try (final Transaction context = Transaction.openOuter()) {
            int inserted = (int) slotStorage.insert(toInsert, stack.getCount(), context);
            context.commit();
            if (inserted <= 0) {
                return stack;
            } else {
                int remainder = stack.getCount() - inserted;
                if (remainder > 0) {
                    return stack.copyWithCount(remainder);
                }
                return ItemStack.EMPTY;
            }
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        SingleSlotStorage<ItemVariant> slotStorage = this.storage.getSlot(slot);
        if (!slotStorage.supportsExtraction() || amount < 0) {
            return ItemStack.EMPTY;
        }

        ItemVariant variant = slotStorage.getResource();
        if (variant.isBlank()) {
            return ItemStack.EMPTY;
        }

        if (simulate) {
            int extracted = (int) slotStorage.simulateExtract(variant, amount, null);
            if (extracted <= 0) {
                return ItemStack.EMPTY;
            } else {
                return slotStorage.getResource().toStack(extracted);
            }
        }

        try (final Transaction context = Transaction.openOuter()) {
            ItemStack extractingType = slotStorage.getResource().toStack();
            int extracted = (int) slotStorage.extract(variant, amount, context);
            context.commit();
            if (extracted <= 0) {
                return ItemStack.EMPTY;
            } else {
                return extractingType.copyWithCount(extracted);
            }
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return (int) this.storage.getSlot(slot).getCapacity();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        SingleSlotStorage<ItemVariant> slotStorage = this.storage.getSlot(slot);
        return slotStorage.supportsInsertion() && slotStorage.simulateInsert(ItemVariant.of(stack), stack.getCount(), null) > 0;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        SingleSlotStorage<ItemVariant> slotStorage = this.storage.getSlot(slot);
        try (final Transaction context = Transaction.openOuter()) {
            // Extract everything from the slot
            slotStorage.extract(slotStorage.getResource(), slotStorage.getCapacity(), context);
            // Then add the stack in to set
            slotStorage.insert(ItemVariant.of(stack), stack.getCount(), context);
            context.commit();
        }
    }

    @Override
    public void onContentsChanged(int slot) {
        if (this.entity != null)
            this.entity.setChanged();
    }
}
