package com.grim3212.assorted.lib.core.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;

public interface INamed extends Nameable {

    void setCustomName(Component name);
}
