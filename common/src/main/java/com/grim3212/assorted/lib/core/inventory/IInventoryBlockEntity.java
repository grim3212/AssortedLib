package com.grim3212.assorted.lib.core.inventory;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface IInventoryBlockEntity extends IInventory<BlockEntity> {
    IPlatformInventoryStorageHandler getStorageHandler();
}
