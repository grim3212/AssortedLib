package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.core.block.IBlockCloneStack;
import com.grim3212.assorted.lib.platform.FabricConfigHelper;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AssortedLibFabric implements ModInitializer {


    @Override
    public void onInitialize() {
        LibConstants.LOG.info(LibConstants.MOD_NAME + " starting up...");

        Services.CONDITIONS.init();
        Services.INGREDIENTS.register();

        FabricConfigHelper.init();

        // TODO(26.2): net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback was
        //  removed. Pick block is resolved on the server now, through PlayerPickItemEvents.BLOCK,
        //  which hands over the ServerPlayer, the BlockPos and the BlockState but *not* the
        //  HitResult the old client side callback carried. IBlockCloneStack still asks for one, so a
        //  BlockHitResult pointing at the centre of the block is synthesised; the exact face and hit
        //  vector the player was looking at are no longer available at this point.
        PlayerPickItemEvents.BLOCK.register((player, pos, state, includeData) -> {
            if (state.getBlock() instanceof IBlockCloneStack extraProperties) {
                final BlockHitResult target = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
                return extraProperties.getCloneItemStack(state, target, player.level(), pos, player);
            }

            // null lets the next listener - and ultimately vanilla - handle the pick.
            return null;
        });
    }

}
