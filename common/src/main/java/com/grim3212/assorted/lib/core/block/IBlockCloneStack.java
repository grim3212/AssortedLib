package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The stack a block hands back when it is picked. Pick block is resolved on the server on both
 * loaders now and carries no hit result, so the face and hit vector a 1.20.1 implementation could
 * read are simply not available to ask for.
 */
public interface IBlockCloneStack {
    ItemStack getCloneItemStack(BlockState state, BlockGetter blockGetter, BlockPos pos, Player player);
}
