package com.grim3212.assorted.lib.mixin.world.level;

import com.grim3212.assorted.lib.core.block.IBlockOnPlayerBreak;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
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

    /**
     * The server half of {@link IBlockOnPlayerBreak}, at the head of {@code destroyBlock} where
     * NeoForge fires its own {@code BreakBlockEvent}. Answering false is what the method already
     * does for a block the player may not break.
     */
    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void assortedlib_canPlayerBreak(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        final BlockState state = this.level.getBlockState(pos);
        if (state.getBlock() instanceof IBlockOnPlayerBreak onPlayerBreak && !onPlayerBreak.canPlayerBreak(state, this.level, pos, this.player)) {
            info.setReturnValue(false);
        }
    }
}
