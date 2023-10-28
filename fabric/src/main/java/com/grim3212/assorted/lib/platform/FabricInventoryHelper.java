package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.core.inventory.IInventoryItem;
import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.inventory.FabricPlatformInventoryStorageHandlerSided;
import com.grim3212.assorted.lib.inventory.FabricPlatformInventoryStorageHandlerUnsided;
import com.grim3212.assorted.lib.inventory.FabricWrappedItemHandler;
import com.grim3212.assorted.lib.inventory.FabricWrappedStorageHandler;
import com.grim3212.assorted.lib.platform.services.IInventoryHelper;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class FabricInventoryHelper implements IInventoryHelper {
    @Override
    public boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        if (a.isEmpty() || !ItemStack.isSameItem(a, b) || a.hasTag() != b.hasTag())
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
                return Optional.of(storageHandler.getItemStorageHandler(null));
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<IItemStorageHandler> getItemStorageHandler(BlockEntity blockEntity, @Nullable Direction direction) {
        if (blockEntity == null || blockEntity.isRemoved())
            return Optional.empty();

        if (blockEntity instanceof IInventoryBlockEntity inventoryBlockEntity) {
            IPlatformInventoryStorageHandler storageHandler = inventoryBlockEntity.getStorageHandler();
            if (storageHandler != null) {
                return Optional.of(storageHandler.getItemStorageHandler(direction));
            }
        }

        Storage<ItemVariant> inventory = ItemStorage.SIDED.find(blockEntity.getLevel(), blockEntity.getBlockPos(), direction);
        if (inventory != null && inventory instanceof InventoryStorage inventoryStorage) {
            // TODO: Look into supporting the base Storage<ItemVariant>
            return Optional.of(new FabricWrappedItemHandler(blockEntity, inventoryStorage));
        }

        if (blockEntity instanceof Container container) {
            return Optional.of(new FabricWrappedItemHandler(blockEntity, InventoryStorage.of(container, direction)));
        }

        return Optional.empty();
    }

    @Override
    public IPlatformInventoryStorageHandler createStorageInventoryHandler(IItemStorageHandler handler) {
        return new FabricPlatformInventoryStorageHandlerUnsided(handler);
    }

    @Override
    public IPlatformInventoryStorageHandler createSidedStorageInventoryHandler(Function<Direction, IItemStorageHandler> handler) {
        return new FabricPlatformInventoryStorageHandlerSided(handler);
    }

    @Override
    public Container wrapStorageHandler(IItemStorageHandler handler) {
        return new FabricWrappedStorageHandler(handler);
    }
}
