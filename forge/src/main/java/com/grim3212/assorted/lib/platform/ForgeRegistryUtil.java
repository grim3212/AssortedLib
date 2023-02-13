package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IRegistryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeMenuType;

import java.util.function.BiFunction;

public class ForgeRegistryUtil implements IRegistryUtil {

    @SuppressWarnings("all")
    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> builder, Block... blocks) {
        return BlockEntityType.Builder.of(builder::apply, blocks).build(null);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        return IForgeMenuType.create(factory::create);
    }
}
