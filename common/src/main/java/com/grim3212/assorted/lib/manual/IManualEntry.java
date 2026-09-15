package com.grim3212.assorted.lib.manual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Implemented by a block, item or entity whose manual page depends on its own state. Anything with a
 * fixed page uses {@link ManualLinks} instead and needs no change to its class.
 * <p>
 * Returns a {@link ManualPageRef} rather than a page so this stays callable from common code.
 */
public interface IManualEntry {

    interface IManualBlock extends IManualEntry {

        @Nullable
        ManualPageRef getManualPage(BlockState state);

        /** Override when the page depends on more than the state. Level and pos are null out of world. */
        @Nullable
        default ManualPageRef getManualPage(@Nullable Level level, @Nullable BlockPos pos, BlockState state) {
            return this.getManualPage(state);
        }
    }

    interface IManualItem extends IManualEntry {

        @Nullable
        ManualPageRef getManualPage(ItemStack stack);
    }

    interface IManualEntity extends IManualEntry {

        @Nullable
        ManualPageRef getManualPage(Entity entity);
    }
}
