package com.grim3212.assorted.lib.manual;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

/**
 * Sections registered in code, for a mod that would rather not ship a {@code section.json}. Call
 * from common init; a {@code section.json} of the same namespace overrides whatever is put here.
 */
public final class ManualRegistry {

    // Concurrent: NeoForge constructs mods in parallel. Index order comes from sorting on read.
    private static final Map<String, ManualSection> SECTIONS = new ConcurrentHashMap<>();

    private ManualRegistry() {
    }

    /** Replaces any section already registered for the same mod id. */
    public static void register(ManualSection section) {
        SECTIONS.put(section.modId(), section);
    }

    public static Optional<ManualSection> section(String modId) {
        return Optional.ofNullable(SECTIONS.get(modId));
    }

    /** Sorted into index order. */
    public static List<ManualSection> sections() {
        return SECTIONS.values().stream()
                .sorted(Comparator.comparingInt(ManualSection::sortOrder).thenComparing(ManualSection::modId))
                .toList();
    }

    public static Collection<String> modIds() {
        return List.copyOf(SECTIONS.keySet());
    }
}
