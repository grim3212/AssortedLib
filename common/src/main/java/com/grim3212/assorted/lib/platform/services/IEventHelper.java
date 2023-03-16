package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.events.GenericEvent;

import java.util.function.Consumer;

public interface IEventHelper {
    void registerEventType(Class<? extends GenericEvent> eventType, Runnable runnable);

    <T extends GenericEvent> void handleEvents(T event);

    // TODO: change events to be able to be registered regardles of if AssortedLib loads first or last
    void registerEvent(Class<? extends GenericEvent> eventType, Consumer<?> handler);
}
