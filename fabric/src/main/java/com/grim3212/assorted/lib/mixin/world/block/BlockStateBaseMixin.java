package com.grim3212.assorted.lib.mixin.world.block;

import com.grim3212.assorted.lib.core.block.IBlockMapColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Shadow
    public abstract Block getBlock();

    @Shadow
    public abstract BlockState asState();

    @Shadow
    public MaterialColor materialColor;


    @Inject(method = "getMapColor", at = @At("HEAD"), cancellable = true)
    public void assortedlib_getMapColor(BlockGetter level, BlockPos pos, CallbackInfoReturnable<MaterialColor> cir) {
        if (getBlock() instanceof IBlockMapColor mapColor) {
            cir.setReturnValue(mapColor.getMapColor(asState(), level, pos, materialColor));
        }
    }

}
