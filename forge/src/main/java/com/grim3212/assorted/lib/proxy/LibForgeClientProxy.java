package com.grim3212.assorted.lib.proxy;

import com.grim3212.assorted.lib.client.events.*;
import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class LibForgeClientProxy implements LibForgeProxy {


    @Override
    public int getFluidColor(FluidInformation fluid) {
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid.fluid());
        return clientFluid.getTintColor();
    }

    @Override
    @SuppressWarnings("removal")
    public void starting() {
        Services.EVENTS.registerEventType(ClientTickEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final TickEvent.ClientTickEvent clientTickEvent) -> {
                ClientTickEvent newEvent;
                if (clientTickEvent.phase == net.minecraftforge.event.TickEvent.Phase.START) {
                    newEvent = new ClientTickEvent.StartClientTickEvent();
                } else {
                    newEvent = new ClientTickEvent.EndClientTickEvent();
                }

                Services.EVENTS.handleEvents(newEvent);
                if (newEvent.isCanceled()) {
                    clientTickEvent.setCanceled(true);
                }
            });
        });

        Services.EVENTS.registerEventType(ClientSetupEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final FMLClientSetupEvent event) -> {
                final ClientSetupEvent clientSetupEvent = new ClientSetupEvent(ItemProperties::register, ItemBlockRenderTypes::setRenderLayer);
                Services.EVENTS.handleEvents(clientSetupEvent);
            });
        });

        Services.EVENTS.registerEventType(RegisterKeyBindEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final RegisterKeyMappingsEvent event) -> {
                final RegisterKeyBindEvent registerKeyBindEvent = new RegisterKeyBindEvent(event::register);
                Services.EVENTS.handleEvents(registerKeyBindEvent);
            });
        });

        Services.EVENTS.registerEventType(RegisterBlockColorEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final RegisterColorHandlersEvent.Block event) -> {
                final RegisterBlockColorEvent registerBlockColorEvent = new RegisterBlockColorEvent(event.getBlockColors(), event::register);
                Services.EVENTS.handleEvents(registerBlockColorEvent);
            });
        });

        Services.EVENTS.registerEventType(RegisterItemColorEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final RegisterColorHandlersEvent.Item event) -> {
                final RegisterItemColorEvent registerItemColorEvent = new RegisterItemColorEvent(event.getItemColors(), event::register);
                Services.EVENTS.handleEvents(registerItemColorEvent);
            });
        });

        Services.EVENTS.registerEventType(RegisterRenderersEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final EntityRenderersEvent.RegisterRenderers event) -> {
                final RegisterRenderersEvent registerRenderersEvent = new RegisterRenderersEvent(event::registerBlockEntityRenderer, event::registerEntityRenderer);
                Services.EVENTS.handleEvents(registerRenderersEvent);
            });
        });

        Services.EVENTS.registerEventType(RegisterEntityLayersEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final EntityRenderersEvent.RegisterLayerDefinitions event) -> {
                final RegisterEntityLayersEvent registerEntityLayersEvent = new RegisterEntityLayersEvent(event::registerLayerDefinition);
                Services.EVENTS.handleEvents(registerEntityLayersEvent);
            });
        });
    }
}
