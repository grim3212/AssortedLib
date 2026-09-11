package com.grim3212.assorted.lib.core.item;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

/**
 * Data components any Assorted mod can put on its items.
 */
public class LibDataComponents {

    public static final RegistryProvider<DataComponentType<?>> DATA_COMPONENTS = RegistryProvider.create(Registries.DATA_COMPONENT_TYPE, LibConstants.MOD_ID);

    public static final IRegistryObject<DataComponentType<ItemDescription>> DESCRIPTION = DATA_COMPONENTS.register("description",
            () -> new DataComponentType.Builder<ItemDescription>().persistent(ItemDescription.CODEC).networkSynchronized(ItemDescription.STREAM_CODEC).build());

    // Called from each loader's entry point.
    public static void init() {
        Services.PLATFORM.showComponentTooltip(DESCRIPTION);
    }
}
