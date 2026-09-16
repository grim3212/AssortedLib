package com.grim3212.assorted.lib.conditions;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * The parts a mod can be turned off in pieces by, and whether each is on right now.
 */
public final class LibParts {

    // Concurrent: NeoForge constructs mods in parallel and each registers its own.
    private static final Map<String, Supplier<Boolean>> PARTS = new ConcurrentHashMap<>();

    private LibParts() {
    }

    public static void register(String part, Supplier<Boolean> enabled) {
        if (PARTS.putIfAbsent(part, enabled) != null) {
            throw new IllegalArgumentException("Can't have registered part with the same name as another: " + part);
        }
    }

    public static boolean isRegistered(String part) {
        return PARTS.containsKey(part);
    }

    public static Set<String> registered() {
        return Set.copyOf(PARTS.keySet());
    }

    /**
     * Whether {@code part} is switched on. A part nothing registered reads as on: an unknown name in
     * a resource pack should leave the content visible rather than hide it silently, and the places
     * where a typo is worth failing over - a recipe condition, and the manual's own datagen - check
     * {@link #isRegistered} first.
     */
    public static boolean isEnabled(String part) {
        Supplier<Boolean> enabled = PARTS.get(part);
        return enabled == null || enabled.get();
    }

    /** Whether every one of them is on; an empty list is unconditional. */
    public static boolean allEnabled(Iterable<String> parts) {
        for (String part : parts) {
            if (!isEnabled(part)) {
                return false;
            }
        }

        return true;
    }
}
