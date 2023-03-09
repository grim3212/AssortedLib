package com.grim3212.assorted.lib.events;

public abstract class ClientTickEvent extends GenericEvent {


    public static class StartClientTickEvent extends ClientTickEvent {

    }

    public static class EndClientTickEvent extends ClientTickEvent {

    }
}
