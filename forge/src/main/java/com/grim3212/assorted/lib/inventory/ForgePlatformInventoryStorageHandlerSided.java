package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ForgePlatformInventoryStorageHandlerSided implements IPlatformInventoryStorageHandler {

    private final Function<Direction, IItemStorageHandler> handler;
    private final Map<Direction, LazyOptional<IItemHandler>> itemHandlers;

    public ForgePlatformInventoryStorageHandlerSided(Function<Direction, IItemStorageHandler> handler) {
        this.handler = handler;
        this.itemHandlers = new HashMap<>();
    }

    public LazyOptional<IItemHandler> getCapability(@Nullable Direction direction) {
        if (!this.itemHandlers.containsKey(direction)) {
            IItemStorageHandler newHandler = this.handler.apply(direction);
            if (newHandler == null) {
                this.itemHandlers.put(direction, LazyOptional.empty());
            } else {
                this.itemHandlers.put(direction, LazyOptional.of(() -> new ForgeItemStorageHandler(newHandler)));
            }
        }
        return this.itemHandlers.get(direction);
    }

    @Override
    public void invalidate() {
        this.itemHandlers.values().forEach(x -> x.invalidate());
    }

    @Override
    public IItemStorageHandler getItemStorageHandler(@Nullable Direction direction) {
        return this.handler.apply(direction);
    }

}
