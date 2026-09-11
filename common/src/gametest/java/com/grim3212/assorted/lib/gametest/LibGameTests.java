package com.grim3212.assorted.lib.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for AssortedLib.
 * <p>
 * Everything downstream sits on this module, so what these pin down is not a feature but a
 * <em>parity</em> claim: that the NeoForge and Fabric implementations of each
 * {@code lib/platform/services} interface answer the same way about the same world. The bodies live
 * in common and are byte-identical on both loaders; only the {@code Registries.TEST_FUNCTION}
 * registration differs, and {@code data/assortedlib/test_instance/*.json} pairs each one with the
 * shared {@code test_box} structure.
 * <p>
 * Client-only services ({@code ClientServices}) are deliberately untested here - a gametest run is a
 * dedicated server, where loading them would fail by design.
 * <p>
 * The tests themselves are split by feature into the {@code *Tests} classes in this package,
 * with shared helpers in {@code LibTestSupport}; this only lists them.
 */
public final class LibGameTests {

    private LibGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        PlatformServiceTests.register(out);
        InventoryTests.register(out);
        FluidTests.register(out);
        IngredientTests.register(out);
        EventTests.register(out);
    }
}
