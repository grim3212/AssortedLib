package com.grim3212.assorted.lib.client.events;

import com.grim3212.assorted.lib.events.GenericEvent;
import net.minecraft.client.KeyMapping;

import java.util.function.Consumer;

public class RegisterKeyBindEvent extends GenericEvent {

    private final Consumer<KeyMapping> consumer;

    public RegisterKeyBindEvent(Consumer<KeyMapping> consumer) {
        this.consumer = consumer;
    }

    public void register(KeyMapping keyMapping) {
        this.consumer.accept(keyMapping);
    }
}
