package com.grim3212.assorted.lib.mixin.block;

import com.grim3212.assorted.lib.events.OnDropStacksEvent;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true)
    // The tool parameter widened from ItemStack to ItemInstance in 26.2. The event still deals in
    // ItemStack, which is what every caller actually passes, so it is narrowed back here rather than
    // widening the event's public type.
    private static void getDroppedStacks(BlockState state, ServerLevel world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemInstance tool, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> returnValue = cir.getReturnValue();

        ItemStack stack = tool instanceof ItemStack itemStack ? itemStack : ItemStack.EMPTY;
        final OnDropStacksEvent onDropStacksEvent = new OnDropStacksEvent(state, world, pos, blockEntity, entity, stack, returnValue);
        Services.EVENTS.handleEvents(onDropStacksEvent);

        if (onDropStacksEvent.isCanceled()) {
            cir.setReturnValue(new ArrayList<>());
        } else {
            cir.setReturnValue(onDropStacksEvent.getDrops());
        }
    }
}
