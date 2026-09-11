package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.events.CorrectToolForDropEvent;
import com.grim3212.assorted.lib.events.GenericEvent;
import com.grim3212.assorted.lib.events.OnDropStacksEvent;
import com.grim3212.assorted.lib.platform.services.IEventHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * The part of {@link IEventHelper} both loaders share. Each event type's hook is installed once,
 * the first time any mod registers a handler for it. Registration runs on NeoForge's parallel mod
 * constructor threads and dispatch on client and server threads at once, hence the synchronized
 * registration and copy-on-write handler lists.
 */
public abstract class EventHelperBase implements IEventHelper {
    private final Map<Class<?>, Runnable> hooks = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<Consumer<?>>> handlers = new ConcurrentHashMap<>();
    private final Set<Class<?>> installed = new HashSet<>();
    private final Set<Class<?>> reportedMissing = new HashSet<>();

    protected EventHelperBase() {
        // Raised straight from the common BlockMixin and ItemStackMixin on both loaders, so there is
        // no loader event to subscribe to. They still need an entry, or a handler for them is
        // reported as one that can never be called.
        this.registerEventType(OnDropStacksEvent.class, () -> {
        });
        this.registerEventType(CorrectToolForDropEvent.class, () -> {
        });
    }

    @Override
    public synchronized void registerEventType(Class<? extends GenericEvent> eventType, Runnable hook) {
        this.hooks.put(eventType, hook);

        // A handler was registered before its hook was: install it now rather than never.
        if (this.handlers.containsKey(eventType)) {
            this.install(eventType);
        }
    }

    @Override
    public synchronized void registerEvent(Class<? extends GenericEvent> eventType, Consumer<?> handler) {
        this.handlers.computeIfAbsent(eventType, type -> new CopyOnWriteArrayList<>()).add(handler);
        this.install(eventType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GenericEvent> void handleEvents(T event) {
        List<Consumer<?>> registered = this.handlers.get(event.getClass());
        if (registered == null) {
            return;
        }

        for (Consumer<?> handler : registered) {
            ((Consumer<T>) handler).accept(event);
        }
    }

    private void install(Class<?> eventType) {
        Runnable hook = this.hooks.get(eventType);
        if (hook == null) {
            if (this.reportedMissing.add(eventType)) {
                LibConstants.LOG.error("Nothing raises " + eventType.getName() + " on this loader, so its handlers will never be called");
            }
            return;
        }

        if (this.installed.add(eventType)) {
            hook.run();
        }
    }
}
