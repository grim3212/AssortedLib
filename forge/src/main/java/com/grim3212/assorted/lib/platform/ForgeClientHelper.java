package com.grim3212.assorted.lib.platform;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.client.events.ClientTickHandler;
import com.grim3212.assorted.lib.platform.services.IClientHelper;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.*;
import java.util.function.Supplier;


public class ForgeClientHelper implements IClientHelper {

    private static final Map<Item, BlockEntityWithoutLevelRenderer> bewlrs = Maps.newConcurrentMap();
    private static final Map<String, Registrations> registrationsMap = Maps.newConcurrentMap();

    @Override
    public <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void registerScreen(MenuType<? extends T> menuType, IPlatformHelper.ScreenFactory<T, S> factory) {
        MenuScreens.register(menuType, factory::create);
    }

    @Override
    public void registerBEWLR(Item item, BlockEntityWithoutLevelRenderer renderer) {
        bewlrs.put(item, renderer);
    }

    @Override
    public <E extends BlockEntity> void registerBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends E>> entityType, BlockEntityRendererProvider<E> entityRendererFactory) {
        getRegistration().blockEntityRenderers.put((Supplier<BlockEntityType<?>>) (Supplier<? extends BlockEntityType<?>>) entityType, entityRendererFactory);
    }

    @Override
    public <E extends Entity> void registerEntityRenderer(Supplier<? extends EntityType<? extends E>> entityType, EntityRendererProvider<E> entityRendererFactory) {
        getRegistration().entityRenderers.put((Supplier<EntityType<?>>) (Supplier<? extends EntityType<?>>) entityType, entityRendererFactory);
    }

    @Override
    public void registerEntityLayer(ModelLayerLocation modelLayerLocation, Supplier<LayerDefinition> layerDefinition) {
        getRegistration().entityLayers.put(modelLayerLocation, layerDefinition);
    }

    @Override
    public void registerBlockColor(BlockColor color, Supplier<List<Block>> blocks) {
        getRegistration().blockColors.put(color, blocks);
    }

    @Override
    public void registerItemColor(ItemColor color, Supplier<List<Item>> items) {
        getRegistration().itemColors.put(color, items);
    }

    @Override
    public BlockColors getBlockColors() {
        return Minecraft.getInstance().getBlockColors();
    }

    @Override
    public ItemColors getItemColors() {
        return Minecraft.getInstance().getItemColors();
    }

    @Override
    public void registerItemProperty(Supplier<Item> item, ResourceLocation location, ClampedItemPropertyFunction itemPropertyFunction) {
        getRegistration().itemProperties.put(item, Pair.of(location, itemPropertyFunction));
    }

    @Override
    public void registerRenderType(Supplier<Block> block, RenderType renderType) {
        getRegistration().renderTypes.put(block, renderType);
    }

    @Override
    public void registerKeyMapping(KeyMapping keyMapping) {
        getRegistration().keyMappings.add(keyMapping);
    }

    @Override
    public void registerClientTickStart(ClientTickHandler handler) {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent orig) -> {
            if (orig.phase == TickEvent.Phase.START) {
                handler.handle(Minecraft.getInstance());
            }
        });
    }

    @Override
    public void registerClientTickEnd(ClientTickHandler handler) {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent orig) -> {
            if (orig.phase == TickEvent.Phase.END) {
                handler.handle(Minecraft.getInstance());
            }
        });
    }

    public static Registrations getRegistration() {
        String modId = ModLoadingContext.get().getActiveContainer().getModId();
        if (registrationsMap.containsKey(modId)) {
            return registrationsMap.get(modId);
        } else {
            Registrations newRegistration = new Registrations();
            registrationsMap.put(modId, newRegistration);
            FMLJavaModLoadingContext.get().getModEventBus().register(newRegistration);
            return newRegistration;
        }
    }

    public static Optional<BlockEntityWithoutLevelRenderer> getRenderer(Item item) {
        return Optional.ofNullable(bewlrs.get(item));
    }

    public static class Registrations {
        public final Map<Supplier<BlockEntityType<?>>, BlockEntityRendererProvider<?>> blockEntityRenderers = new HashMap<>();
        public final Map<Supplier<EntityType<?>>, EntityRendererProvider<?>> entityRenderers = new HashMap<>();
        public final Map<ModelLayerLocation, Supplier<LayerDefinition>> entityLayers = new HashMap<>();
        public final Map<BlockColor, Supplier<List<Block>>> blockColors = new HashMap<>();
        public final Map<ItemColor, Supplier<List<Item>>> itemColors = new HashMap<>();
        public final Map<Supplier<Item>, Pair<ResourceLocation, ClampedItemPropertyFunction>> itemProperties = new HashMap<>();
        public final Map<Supplier<Block>, RenderType> renderTypes = new HashMap<>();
        public final List<KeyMapping> keyMappings = new ArrayList<>();

        @SubscribeEvent
        public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            for (Map.Entry<Supplier<BlockEntityType<?>>, BlockEntityRendererProvider<?>> entry : blockEntityRenderers.entrySet()) {
                event.registerBlockEntityRenderer(entry.getKey().get(), (BlockEntityRendererProvider<BlockEntity>) entry.getValue());
            }

            for (Map.Entry<Supplier<EntityType<?>>, EntityRendererProvider<?>> entry : entityRenderers.entrySet()) {
                event.registerEntityRenderer(entry.getKey().get(), (EntityRendererProvider<Entity>) entry.getValue());
            }
        }

        @SubscribeEvent
        public void registerEntityLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
            for (Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>> entry : entityLayers.entrySet()) {
                event.registerLayerDefinition(entry.getKey(), entry.getValue());
            }
        }

        @SubscribeEvent
        public void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
            for (Map.Entry<BlockColor, Supplier<List<Block>>> entry : blockColors.entrySet()) {
                event.register(entry.getKey(), entry.getValue().get().toArray(Block[]::new));
            }
        }

        @SubscribeEvent
        public void registerItemColors(final RegisterColorHandlersEvent.Item event) {
            for (Map.Entry<ItemColor, Supplier<List<Item>>> entry : itemColors.entrySet()) {
                event.register(entry.getKey(), entry.getValue().get().toArray(ItemLike[]::new));
            }
        }

        @SubscribeEvent
        @SuppressWarnings("removal")
        public void clientSetup(final FMLClientSetupEvent event) {
            for (Map.Entry<Supplier<Item>, Pair<ResourceLocation, ClampedItemPropertyFunction>> entry : itemProperties.entrySet()) {
                ItemProperties.register(entry.getKey().get(), entry.getValue().getFirst(), entry.getValue().getSecond());
            }

            for (Map.Entry<Supplier<Block>, RenderType> entry : renderTypes.entrySet()) {
                ItemBlockRenderTypes.setRenderLayer(entry.getKey().get(), entry.getValue());
            }
        }

        @SubscribeEvent
        public void registerKeyMapping(final RegisterKeyMappingsEvent event) {
            for (KeyMapping key : keyMappings) {
                event.register(key);
            }
        }
    }
}
