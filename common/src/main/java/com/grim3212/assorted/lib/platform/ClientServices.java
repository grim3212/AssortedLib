package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IClientHelper;

public class ClientServices {
    public static final IClientHelper CLIENT = Services.load(IClientHelper.class);
}
