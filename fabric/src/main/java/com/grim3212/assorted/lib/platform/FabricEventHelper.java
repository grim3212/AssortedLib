package com.grim3212.assorted.lib.platform;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.grim3212.assorted.lib.events.GenericEvent;
import com.grim3212.assorted.lib.platform.services.IEventHelper;

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
        Runnable initializer = eventInits.remove(eventType);
        if (initializer != null) {
            initializer.run();
        }
        eventHandlers.put(eventType, handler);
    }
}
