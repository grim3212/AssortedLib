package com.grim3212.assorted.lib.client.events;

import com.grim3212.assorted.lib.events.GenericEvent;

public abstract class ClientTickEvent extends GenericEvent {


    public static class StartClientTickEvent extends ClientTickEvent {

    }

    public static class EndClientTickEvent extends ClientTickEvent {

    }
}
