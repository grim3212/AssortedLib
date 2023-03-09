package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IClientHelper;
import com.grim3212.assorted.lib.platform.services.IKeyBindingHelper;

public class ClientServices {
    public static final IClientHelper CLIENT = Services.load(IClientHelper.class);
    public static final IKeyBindingHelper KEYBINDS = Services.load(IKeyBindingHelper.class);
}
