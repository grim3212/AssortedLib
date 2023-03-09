package com.grim3212.assorted.lib.core.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface IItemCorrectDrops {

    /**
     * ItemStack sensitive version of {@link Item#isCorrectToolForDrops(BlockState)}
     *
     * @param stack The itemstack used to harvest the block
     * @param state The block trying to harvest
     * @return true if the stack can harvest the block
     */
    boolean isCorrectToolForDrops(ItemStack stack, BlockState state);
}
