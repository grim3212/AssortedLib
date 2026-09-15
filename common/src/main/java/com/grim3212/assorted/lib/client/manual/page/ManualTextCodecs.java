package com.grim3212.assorted.lib.client.manual.page;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;

/** Chapter json carries translation keys, never literal text. Line breaks belong in the lang file. */
public final class ManualTextCodecs {

    public static final Codec<Component> TRANSLATABLE = Codec.STRING.xmap(Component::translatable, component -> component.getString());

    private ManualTextCodecs() {
    }
}
