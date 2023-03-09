package com.grim3212.assorted.lib.proxy;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.ForgeKeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class LibForgeClientProxy implements LibForgeProxy {

    @Override
    public void starting() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::registerKeys);
    }

    private void registerKeys(final RegisterKeyMappingsEvent event) {
        for (KeyMapping mapping : ForgeKeyBindingHelper.getRegisteredKeyMappings()) {
            event.register(mapping);
        }
    }

    @Override
    public int getFluidColor(FluidInformation fluid) {
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid.fluid());
        return clientFluid.getTintColor();
    }
}
