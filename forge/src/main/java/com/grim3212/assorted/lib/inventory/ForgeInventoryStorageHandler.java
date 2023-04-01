package com.grim3212.assorted.lib.inventory;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.core.inventory.IInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ForgeInventoryStorageHandler implements IInventoryStorageHandler {

    private final IItemStorageHandler handler;
    private final Map<Direction, LazyOptional<IItemHandler>> sidedWrappers = Maps.newEnumMap(Direction.class);

    public ForgeInventoryStorageHandler(IItemStorageHandler handler, Direction... sides) {
        this.handler = handler;

        for (Direction direction : sides) {
            this.sidedWrappers.put(direction, LazyOptional.of(() -> new ForgeItemStorageHandler(handler)));
        }
    }

    public LazyOptional<IItemHandler> getCapability(@Nullable Direction side) {
        if (this.sidedWrappers.containsKey(side))
            return this.sidedWrappers.get(side).cast();
        else
            return LazyOptional.empty();
    }

    @Override
    public void invalidate() {
        this.sidedWrappers.values().forEach(LazyOptional::invalidate);
    }

    @Override
    public IItemStorageHandler getItemStorageHandler() {
        return this.handler;
    }

}
