package com.grim3212.assorted.lib.core.inventory;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * Used to store any extra information a specific Platform may need
 * Example: Forge stores the Capabilities and invalidates them using this
 */
public interface IPlatformInventoryStorageHandler {
    void invalidate();

    IItemStorageHandler getItemStorageHandler(@Nullable Direction direction);
}
