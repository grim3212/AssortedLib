package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.core.inventory.IInventoryItem;
import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.inventory.FabricPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.inventory.FabricWrappedItemHandler;
import com.grim3212.assorted.lib.platform.services.IInventoryHelper;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FabricInventoryHelper implements IInventoryHelper {
    @Override
    public boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        if (a.isEmpty() || !a.sameItem(b) || a.hasTag() != b.hasTag())
            return false;

        return (!a.hasTag() || a.getTag().equals(b.getTag()));
    }

    @Override
    public ItemStack copyStackWithSize(@NotNull ItemStack itemStack, int size) {
        if (size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }

    @Override
    public Optional<IItemStorageHandler> getItemStorageHandler(ItemStack stack) {
        if (stack.isEmpty())
            return Optional.empty();

        if (stack.getItem() instanceof IInventoryItem itemStackStorage) {
            IPlatformInventoryStorageHandler storageHandler = itemStackStorage.getStorageHandler(stack);
            if (storageHandler != null) {
                return Optional.of(storageHandler.getItemStorageHandler());
            }
        }

        // TODO: Maybe look into trying to automatically generate a handler for other item storages that follow the same
        // inventory on an ItemStack structure
        return Optional.empty();
    }

    @Override
    public Optional<IItemStorageHandler> getItemStorageHandler(BlockEntity blockEntity, @Nullable Direction direction) {
        if (blockEntity == null || blockEntity.isRemoved())
            return Optional.empty();

        if (blockEntity instanceof IInventoryBlockEntity inventoryBlockEntity) {
            IPlatformInventoryStorageHandler storageHandler = inventoryBlockEntity.getStorageHandler();
            if (storageHandler != null) {
                return Optional.of(storageHandler.getItemStorageHandler());
            }
        }

        if (blockEntity instanceof Container container) {
            return Optional.of(new FabricWrappedItemHandler(InventoryStorage.of(container, direction)));
        }

        return Optional.empty();
    }

    @Override
    public IPlatformInventoryStorageHandler createStorageInventoryHandler(IItemStorageHandler handler) {
        return new FabricPlatformInventoryStorageHandler(handler);
    }
}
