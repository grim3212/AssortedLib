package com.grim3212.assorted.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ItemUtil {

    /**
     * Destroys a block
     * If the Item in the main hand does not provide drops then the block
     * will be broken without any drops
     *
     * @param blockPos
     * @param level
     * @param player
     * @return
     */
    public static boolean destroyBlock(BlockPos blockPos, Level level, Player player) {
        BlockState blockState = level.getBlockState(blockPos);
        if (!player.getMainHandItem().getItem().canAttackBlock(blockState, level, blockPos, player)) {
            return false;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            Block block = blockState.getBlock();

            if (block instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {
                level.sendBlockUpdated(blockPos, blockState, blockState, 3);
                return false;
            } else {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (player.blockActionRestricted(level, blockPos, serverPlayer.gameMode.getGameModeForPlayer())) {
                        return false;
                    }
                } else {
                    if (player.isSpectator()) {
                        return false;
                    }
                }

                block.playerWillDestroy(level, blockPos, blockState, player);
                boolean flag = level.removeBlock(blockPos, false);
                if (flag) {
                    block.destroy(level, blockPos, blockState);
                }

                if (player.isCreative()) {
                    return true;
                } else {
                    ItemStack mainHandItem = player.getMainHandItem();
                    ItemStack mainHandCopy = mainHandItem.copy();
                    boolean hasCorrectToolForDrops = player.hasCorrectToolForDrops(blockState);
                    mainHandItem.mineBlock(level, blockState, blockPos, player);
                    if (flag && hasCorrectToolForDrops) {
                        block.playerDestroy(level, player, blockPos, blockState, blockEntity, mainHandCopy);
                    }

                    return true;
                }
            }
        }
    }

}
