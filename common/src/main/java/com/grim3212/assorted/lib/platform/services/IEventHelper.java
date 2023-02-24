package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.events.GenericEvent;

import java.util.function.Consumer;

public interface IEventHelper {
    void registerEventType(Class<? extends GenericEvent> eventType, Runnable runnable);

    <T extends GenericEvent> void handleEvents(T event);

    void registerEvent(Class<? extends GenericEvent> eventType, Consumer<?> handler);

}
