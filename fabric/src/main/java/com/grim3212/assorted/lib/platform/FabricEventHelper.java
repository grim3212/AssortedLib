package com.grim3212.assorted.lib.platform;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.events.FabricLootTableModificationContext;
import com.grim3212.assorted.lib.events.GenericEvent;
import com.grim3212.assorted.lib.events.LootTableModifyEvent;
import com.grim3212.assorted.lib.events.UseBlockEvent;
import com.grim3212.assorted.lib.platform.services.IEventHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FabricEventHelper implements IEventHelper {
    // The events that trigger our registered events
    private final Map<Class<?>, Runnable> eventInits = new HashMap<>();
    private final Multimap<Class<?>, Consumer<?>> eventHandlers = ArrayListMultimap.create();

    @Override
    public void registerEventType(Class<? extends GenericEvent> eventType, Runnable runnable) {
        eventInits.put(eventType, runnable);
    }

    public <T extends GenericEvent> void handleEvents(T event) {
        for (Consumer<?> h : eventHandlers.get(event.getClass())) {
            ((Consumer<T>) h).accept(event);
        }
    }

    @Override
    public void registerEvent(Class<? extends GenericEvent> eventType, Consumer<?> handler) {
        if (!initialized) {
            this.init();
        }

        Runnable initializer = eventInits.remove(eventType);
        if (initializer != null) {
            initializer.run();
        } else {
            LibConstants.LOG.error("Could not find event initializer for " + eventType.getName());
        }
        eventHandlers.put(eventType, handler);
    }

    private boolean initialized = false;

    private void init() {
        // Fabric doesn't guarantee load order so prime these instead of relying on Assorted Lib to load first
        this.initialized = true;

        Services.EVENTS.registerEventType(UseBlockEvent.class, () -> UseBlockCallback.EVENT.register((Player player, Level level, InteractionHand hand, BlockHitResult hitResult) -> {
            final UseBlockEvent event = new UseBlockEvent(player, level, hand, hitResult);
            Services.EVENTS.handleEvents(event);
            if (event.isCanceled()) {
                return InteractionResult.FAIL;
            }

            return event.getInteractionResult();
        }));

        // Loot API v3 hands over the table's key and its builder rather than the loaded table itself -
        // there is no loot manager to look the table up in any more - so the event's table is built from
        // the builder as it stands before this modification runs.
        Services.EVENTS.registerEventType(LootTableModifyEvent.class, () -> LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            final LootTableModifyEvent event = new LootTableModifyEvent(tableBuilder.build(), key.identifier(), new FabricLootTableModificationContext(tableBuilder), source.isBuiltin());
            Services.EVENTS.handleEvents(event);
        }));
    }
}
