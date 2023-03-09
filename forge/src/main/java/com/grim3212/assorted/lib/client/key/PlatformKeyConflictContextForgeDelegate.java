package com.grim3212.assorted.lib.client.key;

import net.minecraftforge.client.settings.IKeyConflictContext;

public class PlatformKeyConflictContextForgeDelegate implements IKeyConflictContext {
    private final IKeyConflictHelper delegate;

    public PlatformKeyConflictContextForgeDelegate(final IKeyConflictHelper delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isActive() {
        return delegate.isActive();
    }

    @Override
    public boolean conflicts(final IKeyConflictContext other) {
        return delegate.conflicts(new ForgeKeyConflictContextPlatformDelegate(other));
    }
}
