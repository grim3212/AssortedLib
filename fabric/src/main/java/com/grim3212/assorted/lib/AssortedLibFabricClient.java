package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.client.events.*;
import com.grim3212.assorted.lib.core.block.IBlockExtraProperties;
import com.grim3212.assorted.lib.mixin.client.AccessorMinecraft;
import com.grim3212.assorted.lib.platform.FabricNetworkHelper;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public class AssortedLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> FabricNetworkHelper.initializeClientHandlers());
        ClientLifecycleEvents.CLIENT_STARTED.register(this::loadComplete);

        ClientPickBlockGatherCallback.EVENT.register((player, result) -> {
            if (result instanceof BlockHitResult blockHitResult
                    && Minecraft.getInstance().level != null
                    && Minecraft.getInstance().level.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof IBlockExtraProperties extraProperties) {
                return extraProperties.getCloneItemStack(
                        Minecraft.getInstance().level.getBlockState(blockHitResult.getBlockPos()),
                        result,
                        Minecraft.getInstance().level,
                        ((BlockHitResult) result).getBlockPos(),
                        player
                );
            }

            return ItemStack.EMPTY;
        });

        Services.EVENTS.registerEventType(ClientSetupEvent.class, () -> {
            final ClientSetupEvent clientSetupEvent = new ClientSetupEvent(ItemProperties::register, BlockRenderLayerMap.INSTANCE::putBlock);
            Services.EVENTS.handleEvents(clientSetupEvent);
        });

        Services.EVENTS.registerEventType(RegisterRenderersEvent.class, () -> {
            final RegisterRenderersEvent registerBERs = new RegisterRenderersEvent(BlockEntityRendererRegistry::register, EntityRendererRegistry::register);
            Services.EVENTS.handleEvents(registerBERs);
        });

        Services.EVENTS.registerEventType(RegisterEntityLayersEvent.class, () -> {
            final RegisterEntityLayersEvent registerEntityLayersEvent = new RegisterEntityLayersEvent((type, supplier) -> EntityModelLayerRegistry.registerModelLayer(type, supplier::get));
            Services.EVENTS.handleEvents(registerEntityLayersEvent);
        });

        Services.EVENTS.registerEventType(RegisterKeyBindEvent.class, () -> {
            final RegisterKeyBindEvent registerKeyBindEvent = new RegisterKeyBindEvent(KeyBindingHelper::registerKeyBinding);
            Services.EVENTS.handleEvents(registerKeyBindEvent);
        });
    }

    private void loadComplete(Minecraft mc) {
        Services.EVENTS.registerEventType(RegisterBlockColorEvent.class, () -> {
            final RegisterBlockColorEvent registerBlockColorEvent = new RegisterBlockColorEvent(mc.getBlockColors(), ColorProviderRegistry.BLOCK::register);
            Services.EVENTS.handleEvents(registerBlockColorEvent);
        });

        Services.EVENTS.registerEventType(RegisterItemColorEvent.class, () -> {
            final RegisterItemColorEvent registerItemColorEvent = new RegisterItemColorEvent(((AccessorMinecraft) mc).assortedlib_getItemColors(), ColorProviderRegistry.ITEM::register);
            Services.EVENTS.handleEvents(registerItemColorEvent);
        });
    }
}
