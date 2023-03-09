package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.events.ClientTickEvent;
import com.grim3212.assorted.lib.events.UseBlockEvent;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class AssortedLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Services.EVENTS.registerEventType(UseBlockEvent.class, () -> UseBlockCallback.EVENT.register((Player player, Level level, InteractionHand hand, BlockHitResult hitResult) -> {
            final UseBlockEvent event = new UseBlockEvent(player, level, hand, hitResult);
            Services.EVENTS.handleEvents(event);
            return event.getInteractionResult();
        }));

        Services.EVENTS.registerEventType(ClientTickEvent.class, () -> ClientTickEvents.START_CLIENT_TICK.register(instance -> {
            final ClientTickEvent event = new ClientTickEvent.StartClientTickEvent();
            Services.EVENTS.handleEvents(event);
        }));

        Services.EVENTS.registerEventType(ClientTickEvent.class, () -> ClientTickEvents.END_CLIENT_TICK.register(instance -> {
            final ClientTickEvent event = new ClientTickEvent.EndClientTickEvent();
            Services.EVENTS.handleEvents(event);
        }));

        Services.CONDITIONS.init();
        Services.INGREDIENTS.register();
    }
}
