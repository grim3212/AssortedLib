package com.grim3212.assorted.lib.mixin.client.multiplayer;

import com.grim3212.assorted.lib.core.block.IBlockOnPlayerBreak;
import com.grim3212.assorted.lib.core.block.IBlockSoundType;
import com.grim3212.assorted.lib.events.EntityInteractEvent;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeWorldlyBlockMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
            method = "continueDestroyBlock",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"
            ),
            ordinal = 0
    )
    private SoundType assortedlib_injectGetBlockStateSoundType(final SoundType current, BlockPos pPosBlock, Direction pDirectionFacing) {
        final BlockState blockState = minecraft.level.getBlockState(pPosBlock);
        if (blockState.getBlock() instanceof IBlockSoundType extraProperties) {
            return extraProperties.getSoundType(blockState, Minecraft.getInstance().level, pPosBlock, Minecraft.getInstance().player);
        }
        return current;
    }

    @Inject(method = "interact",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void assortedlib_entityInteract(Player player, Entity entity, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        final EntityInteractEvent event = new EntityInteractEvent(player, interactionHand, entity);
        Services.EVENTS.handleEvents(event);

        if (event.getInteractionResult() == InteractionResult.SUCCESS) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void assortedlib_onDestroyedByPlayer(BlockPos pos, CallbackInfoReturnable<Boolean> cir, Level level, BlockState blockState, Block block) {
        if (blockState.getBlock() instanceof IBlockOnPlayerBreak extraProperties) {
            FluidState fluidstate = level.getFluidState(pos);
            boolean flag = extraProperties.onDestroyedByPlayer(blockState, level, pos, minecraft.player, false, fluidstate);
            if (flag) {
                block.destroy(level, pos, blockState);
            }

            cir.setReturnValue(flag);
        }
    }
}
