package com.grim3212.assorted.lib.client.manual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Page types a chapter's {@code type} field can name. Register from client init. */
public final class ManualPageTypes {

    // Concurrent: NeoForge constructs mods in parallel and each registers its own.
    private static final Map<Identifier, MapCodec<? extends ManualPage>> BY_ID = new ConcurrentHashMap<>();
    private static final Map<MapCodec<? extends ManualPage>, Identifier> BY_CODEC = new ConcurrentHashMap<>();

    /**
     * A map codec so a page's fields sit beside the {@code id} {@link ManualPageEntry} adds rather
     * than nesting. Dispatch resolves per use, since client init order across mods is not promised.
     */
    public static final MapCodec<ManualPage> MAP_CODEC = Identifier.CODEC
            .dispatchMap("type", page -> BY_CODEC.get(page.codec()), ManualPageTypes::codecOf);

    public static final Codec<ManualPage> CODEC = MAP_CODEC.codec();

    private ManualPageTypes() {
    }

    public static void register(Identifier id, MapCodec<? extends ManualPage> codec) {
        BY_ID.put(id, codec);
        BY_CODEC.put(codec, id);
    }

    private static MapCodec<? extends ManualPage> codecOf(Identifier id) {
        MapCodec<? extends ManualPage> codec = BY_ID.get(id);
        if (codec == null) {
            throw new IllegalArgumentException("Unknown manual page type: " + id);
        }
        return codec;
    }

    public static boolean isRegistered(Identifier id) {
        return BY_ID.containsKey(id);
    }
}
