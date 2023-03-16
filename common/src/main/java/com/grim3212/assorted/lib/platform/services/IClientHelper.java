package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.client.events.ClientTickHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Supplier;

public interface IClientHelper {

    <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void registerScreen(MenuType<? extends T> menuType, IPlatformHelper.ScreenFactory<T, S> factory);

    void registerBEWLR(final Item item, final BlockEntityWithoutLevelRenderer renderer);

    <E extends BlockEntity> void registerBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends E>> entityType, BlockEntityRendererProvider<E> entityRendererFactory);

    <E extends Entity> void registerEntityRenderer(Supplier<? extends EntityType<? extends E>> entityType, EntityRendererProvider<E> entityRendererFactory);

    void registerEntityLayer(ModelLayerLocation modelLayerLocation, Supplier<LayerDefinition> layerDefinition);

    void registerBlockColor(BlockColor color, Supplier<List<Block>> blocks);

    void registerItemColor(ItemColor color, Supplier<List<Item>> items);

    BlockColors getBlockColors();

    ItemColors getItemColors();

    void registerItemProperty(Supplier<Item> item, ResourceLocation location, ClampedItemPropertyFunction itemPropertyFunction);

    void registerRenderType(Supplier<Block> block, RenderType renderType);

    void registerKeyMapping(KeyMapping keyMapping);

    void registerClientTickStart(ClientTickHandler handler);

    void registerClientTickEnd(ClientTickHandler handler);
}
