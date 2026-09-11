package com.grim3212.assorted.lib.mixin.block;

import com.grim3212.assorted.lib.core.block.IBlockEntityWithModelData;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Re-renders an {@link IBlockEntityWithModelData} block entity whenever the client loads new data
 * into it (a sync packet and an update tag both end in {@code loadWithComponents}). Neither loader
 * does this - both only read model data at the section's next rebuild - so a block whose look lives
 * in its block entity changed only for the player who changed it.
 */
@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "loadWithComponents", at = @At("TAIL"))
    private void assortedlib_refreshModelDataOnLoad(ValueInput input, CallbackInfo ci) {
        BlockEntity self = (BlockEntity) (Object) this;
        Level level = self.getLevel();
        if (self instanceof IBlockEntityWithModelData && level != null && level.isClientSide()) {
            // The refresh re-reads the model data; the block update re-renders the section with it.
            ClientServices.MODELS.requestModelDataRefresh(self);
            BlockState state = self.getBlockState();
            level.sendBlockUpdated(self.getBlockPos(), state, state, 0);
        }
    }
}
