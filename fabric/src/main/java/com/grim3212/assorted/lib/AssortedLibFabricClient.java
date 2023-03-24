package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.core.block.IBlockExtraProperties;
import com.grim3212.assorted.lib.platform.FabricNetworkHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public class AssortedLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            FabricNetworkHelper.initializeClientHandlers();
        });

        ClientPickBlockGatherCallback.EVENT.register((player, result) -> {
            if (result instanceof BlockHitResult blockHitResult
                    && Minecraft.getInstance().level != null
                    && Minecraft.getInstance().level.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof IBlockExtraProperties extraProperties) {
                return extraProperties.getCloneItemStack(
                        Minecraft.getInstance().level.getBlockState(blockHitResult.getBlockPos()),
                        result,
                        Minecraft.getInstance().level,
                        ((BlockHitResult) result).getBlockPos(),
                        player
                );
            }

            return ItemStack.EMPTY;
        });
    }
}
