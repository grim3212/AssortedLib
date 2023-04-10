package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.core.inventory.IInventoryItem;
import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.inventory.ForgeItemStorageHandler;
import com.grim3212.assorted.lib.inventory.ForgePlatformInventoryStorageHandlerSided;
import com.grim3212.assorted.lib.inventory.ForgePlatformInventoryStorageHandlerUnsided;
import com.grim3212.assorted.lib.inventory.ForgeWrappedItemHandler;
import com.grim3212.assorted.lib.platform.services.IInventoryHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class ForgeInventoryHelper implements IInventoryHelper {
    @Override
    public boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        return ItemHandlerHelper.canItemStacksStack(a, b);
    }

    @Override
    public ItemStack copyStackWithSize(@NotNull ItemStack itemStack, int size) {
        return ItemHandlerHelper.copyStackWithSize(itemStack, size);
    }

    @Override
    public Optional<IItemStorageHandler> getItemStorageHandler(ItemStack stack) {
        var capability = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
        if (capability.isPresent()) {
            IItemHandler itemHandler = capability.resolve().get();
            if (itemHandler instanceof ForgeItemStorageHandler forgeItemStorageHandler) {
                return Optional.of(forgeItemStorageHandler.getStorage());
            } else {
                return Optional.of(new ForgeWrappedItemHandler(null, itemHandler));
            }
        }

        // If we somehow fail to get it from capability try to see if its one of our own
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
        var capability = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction);
        if (capability.isPresent()) {
            IItemHandler itemHandler = capability.resolve().get();
            if (itemHandler instanceof ForgeItemStorageHandler forgeItemStorageHandler) {
                return Optional.of(forgeItemStorageHandler.getStorage());
            } else {
                return Optional.of(new ForgeWrappedItemHandler(blockEntity, itemHandler));
            }
        }

        // If we somehow fail to get it from capability try to see if its one of our own
        if (blockEntity instanceof IInventoryBlockEntity inventoryBlockEntity) {
            IPlatformInventoryStorageHandler storageHandler = inventoryBlockEntity.getStorageHandler();
            if (storageHandler != null) {
                return Optional.of(storageHandler.getItemStorageHandler(direction));
            }
        }

        return Optional.empty();
    }

    @Override
    public IPlatformInventoryStorageHandler createStorageInventoryHandler(IItemStorageHandler handler) {
        return new ForgePlatformInventoryStorageHandlerUnsided(handler);
    }

    @Override
    public IPlatformInventoryStorageHandler createSidedStorageInventoryHandler(Function<Direction, IItemStorageHandler> handler) {
        return new ForgePlatformInventoryStorageHandlerSided(handler);
    }
}
