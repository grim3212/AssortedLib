package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.inventory.ForgeWrappedItemHandler;
import com.grim3212.assorted.lib.platform.services.IInventoryHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
        return capability.isPresent() ? Optional.of(new ForgeWrappedItemHandler(capability.resolve().get())) : Optional.empty();
    }

    @Override
    public Optional<IItemStorageHandler> getItemStorageHandler(BlockEntity blockEntity, @Nullable Direction direction) {
        var capability = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction);
        return capability.isPresent() ? Optional.of(new ForgeWrappedItemHandler(capability.resolve().get())) : Optional.empty();
    }
}
