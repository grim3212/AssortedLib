package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class ForgeItemStorageHandler implements IItemHandlerModifiable {

    private final IItemStorageHandler storage;

    public ForgeItemStorageHandler(@NotNull IItemStorageHandler storage) {
        this.storage = storage;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.storage.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return this.storage.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.storage.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.storage.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.storage.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.storage.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.storage.isItemValid(slot, stack);
    }
}
