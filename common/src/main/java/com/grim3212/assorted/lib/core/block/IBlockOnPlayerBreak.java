package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block that can refuse to be broken by a player, to make the click do something else instead.
 * Asked at the top of {@code destroyBlock} on both sides - NeoForge from its {@code BreakBlockEvent},
 * Fabric from AssortedLib's two game mode mixins - so a refusal costs nothing: the block is intact
 * and {@code playerWillDestroy} has not spawned the break particles or played the sound. Deliberately
 * not NeoForge's {@code IBlockExtension#onDestroyedByPlayer}, which runs after those particles and
 * whose signature a block can silently stop matching.
 */
public interface IBlockOnPlayerBreak {
    /**
     * @return false to leave the block where it is, true to let the break run as usual.
     */
    boolean canPlayerBreak(BlockState state, Level level, BlockPos pos, Player player);
}
