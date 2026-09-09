package com.grim3212.assorted.lib.mixin.world.level;

import com.grim3212.assorted.lib.core.block.IBlockOnPlayerBreak;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {

    @Shadow
    protected ServerLevel level;
    @Shadow
    protected ServerPlayer player;

    @Shadow
    protected abstract boolean isCreative();

    // 26.2 reordered destroyBlock's locals: at the playerWillDestroy call the live slots are
    // 2 BlockEntity, 3 Block and 5 BlockState, with slot 4 not yet assigned - a gap a
    // LocalCapture list cannot describe. Nothing in the method has touched the world yet at
    // that point, so the two values this needs are read straight back off the level instead.
    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;"), cancellable = true)
    private void onDestroy(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        final BlockState state = this.level.getBlockState(pos);
        if (state.getBlock() instanceof IBlockOnPlayerBreak extraProperties) {
            if (this.isCreative()) {
                this.removeBlock(state, extraProperties, pos, false);
                info.setReturnValue(true);
                return;
            }
            final BlockEntity blockEntity = this.level.getBlockEntity(pos);
            ItemStack itemStack = this.player.getMainHandItem();
            ItemStack itemStack2 = itemStack.copy();
            boolean canHarvest = this.player.hasCorrectToolForDrops(state);
            itemStack.mineBlock(this.level, state, pos, this.player);

            boolean removed = this.removeBlock(state, extraProperties, pos, canHarvest);

            if (canHarvest && removed) {
                state.getBlock().playerDestroy(this.level, this.player, pos, state, blockEntity, itemStack2);
            }

            info.setReturnValue(true);
        }
    }

    private boolean removeBlock(BlockState state, IBlockOnPlayerBreak extraProperties, BlockPos arg, boolean canHarvest) {
        boolean removed = extraProperties.onDestroyedByPlayer(state, this.level, arg, this.player, canHarvest, this.level.getFluidState(arg));
        if (removed) {
            state.getBlock().destroy(this.level, arg, state);
        }
        return removed;
    }
}
