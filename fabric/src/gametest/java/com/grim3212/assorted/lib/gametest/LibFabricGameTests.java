package com.grim3212.assorted.lib.gametest;

import com.grim3212.assorted.lib.LibConstants;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * Puts this mod's test functions into {@code BuiltInRegistries.TEST_FUNCTION}.
 * <p>
 * Fabric has no annotation that discovers a class the way FML's {@code @EventBusSubscriber} does,
 * and nothing in {@code main} may reference the gametest source set - that is what would drag the
 * tests into the shipped jar, and from there into every downstream mod. So the gametest source set
 * carries its own {@code fabric.mod.json} and loads as a second, development-only mod that depends
 * on this one. It is not built into any release artifact.
 * <p>
 * Fabric's own {@code @GameTest} annotation is not used: it defines the test <em>instance</em> as
 * well as the function, which would duplicate the {@code test_instance} jsons that NeoForge and
 * Fabric already share.
 */
public class LibFabricGameTests implements ModInitializer {

    @Override
    public void onInitialize() {
        LibGameTests.forEach((name, function) ->
                Registry.register(BuiltInRegistries.TEST_FUNCTION, Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, name), function));
    }
}
