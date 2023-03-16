package com.grim3212.assorted.lib.events;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class CorrectToolForDropEvent extends GenericEvent {
    private final BlockState state;
    private final ItemStack stack;

    private Optional<Boolean> response = Optional.empty();

    public CorrectToolForDropEvent(BlockState state, ItemStack stack) {
        this.state = state;
        this.stack = stack;
    }

    public BlockState getState() {
        return state;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setResponse(Optional<Boolean> response) {
        this.response = response;
    }

    public Optional<Boolean> getResponse() {
        return response;
    }
}
