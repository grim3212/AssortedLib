package com.grim3212.assorted.lib.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * If cancelled no items will be dropped
 */
public class OnDropStacksEvent extends GenericEvent {

    private final BlockState state;
    private final ServerLevel world;
    private final BlockPos pos;
    private final BlockEntity blockEntity;
    private final Entity entity;
    private final ItemStack stack;
    private List<ItemStack> drops;

    public OnDropStacksEvent(BlockState state, ServerLevel world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, List<ItemStack> currentDroppedStacks) {
        this.state = state;
        this.world = world;
        this.pos = pos;
        this.blockEntity = blockEntity;
        this.entity = entity;
        this.stack = stack;
        this.drops = currentDroppedStacks;
    }

    public BlockState getState() {
        return state;
    }

    public ServerLevel getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    public Entity getEntity() {
        return entity;
    }

    public ItemStack getStack() {
        return stack;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public void setDrops(List<ItemStack> drops) {
        this.drops = drops;
    }
}
