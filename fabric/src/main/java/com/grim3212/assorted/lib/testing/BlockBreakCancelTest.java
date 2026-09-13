package com.grim3212.assorted.lib.testing;

import com.grim3212.assorted.lib.core.block.IBlockOnPlayerBreak;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBreakCancelTest extends Block implements IBlockOnPlayerBreak {
    public BlockBreakCancelTest(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canPlayerBreak(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }
}
