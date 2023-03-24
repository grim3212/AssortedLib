package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IClientFluidHelper;
import com.grim3212.assorted.lib.platform.services.IClientHelper;
import com.grim3212.assorted.lib.platform.services.IClientModelHelper;
import com.grim3212.assorted.lib.platform.services.IKeyBindingHelper;

public class ClientServices {
    public static final IClientHelper CLIENT = Services.load(IClientHelper.class);
    public static final IKeyBindingHelper KEYBINDS = Services.load(IKeyBindingHelper.class);
    public static final IClientFluidHelper FLUIDS = Services.load(IClientFluidHelper.class);
    public static final IClientModelHelper MODELS = Services.load(IClientModelHelper.class);
}
