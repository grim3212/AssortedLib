package com.grim3212.assorted.lib.client.key;

import net.minecraftforge.client.settings.IKeyConflictContext;

public class ForgeKeyConflictContextPlatformDelegate implements IKeyConflictHelper {

    private final IKeyConflictContext delegate;

    public ForgeKeyConflictContextPlatformDelegate(final IKeyConflictContext delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isActive() {
        return delegate.isActive();
    }

    @Override
    public boolean conflicts(final IKeyConflictHelper other) {
        if (!(other instanceof ForgeKeyConflictContextPlatformDelegate))
            throw new IllegalArgumentException("The given key conflict context is not compatible with the forge platform!");

        return delegate.conflicts(((ForgeKeyConflictContextPlatformDelegate) other).getDelegate());
    }

    public IKeyConflictContext getDelegate() {
        return delegate;
    }
}
