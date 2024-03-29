package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class ForgePlatformInventoryStorageHandlerUnsided implements IPlatformInventoryStorageHandler {

    private final IItemStorageHandler handler;
    private final LazyOptional<IItemHandler> optionalItemHandler;

    public ForgePlatformInventoryStorageHandlerUnsided(IItemStorageHandler handler) {
        this.handler = handler;
        this.optionalItemHandler = LazyOptional.of(() -> new ForgeItemStorageHandler(handler));
    }

    public LazyOptional<IItemHandler> getCapability() {
        return this.optionalItemHandler;
    }

    @Override
    public void invalidate() {
        this.optionalItemHandler.invalidate();
    }

    @Override
    public IItemStorageHandler getItemStorageHandler(@Nullable Direction direction) {
        return this.handler;
    }

}
