package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.client.key.ForgeKeyConflictContextPlatformDelegate;
import com.grim3212.assorted.lib.client.key.IKeyConflictHelper;
import com.grim3212.assorted.lib.client.key.KeyModifier;
import com.grim3212.assorted.lib.client.key.PlatformKeyConflictContextForgeDelegate;
import com.grim3212.assorted.lib.platform.services.IKeyBindingHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ForgeKeyBindingHelper implements IKeyBindingHelper {

    // A key mapping's group is a KeyMapping.Category: a record around an Identifier whose label is
    // derived from that id as "key.category.<namespace>.<path>". Cached so the same id always yields
    // the same category instance, which is what ForgeClientHelper deduplicates registration on.
    private static final Map<Identifier, KeyMapping.Category> CATEGORIES = new ConcurrentHashMap<>();

    @Override
    public IKeyConflictHelper getGuiKeyConflictContext() {
        return new ForgeKeyConflictContextPlatformDelegate(KeyConflictContext.GUI);
    }

    @Override
    public IKeyConflictHelper getInGameKeyConflictContext() {
        return new ForgeKeyConflictContextPlatformDelegate(KeyConflictContext.IN_GAME);
    }

    @Override
    public KeyMapping createNew(
            final String translationKey, final IKeyConflictHelper keyConflictContext, final InputConstants.Type inputType, final int key, final Identifier category) {
        return new KeyMapping(
                translationKey,
                new PlatformKeyConflictContextForgeDelegate(keyConflictContext),
                inputType,
                key,
                getCategory(category)
        );
    }

    @Override
    public KeyMapping createNew(
            final String translationKey,
            final IKeyConflictHelper keyConflictContext,
            final KeyModifier keyModifier,
            final InputConstants.Type inputType,
            final int key,
            final Identifier category) {
        return new KeyMapping(
                translationKey,
                new PlatformKeyConflictContextForgeDelegate(keyConflictContext),
                makePlatformSpecific(keyModifier),
                inputType,
                key,
                getCategory(category)
        );
    }

    @Override
    public boolean isKeyConflictOfActive(final KeyMapping keybinding) {
        return keybinding.getKeyConflictContext().isActive();
    }

    @Override
    public boolean isKeyModifierActive(final KeyMapping keybinding) {
        return keybinding.getKeyModifier().isActive(keybinding.getKeyConflictContext());
    }

    /**
     * The category for a category id. Categories are not registered here; every mapping carries its
     * own, and {@link ForgeClientHelper} registers the distinct ones it sees when
     * {@code RegisterKeyMappingsEvent} fires.
     */
    public static KeyMapping.Category getCategory(final Identifier category) {
        return CATEGORIES.computeIfAbsent(category, KeyMapping.Category::new);
    }

    private static net.neoforged.neoforge.client.settings.KeyModifier makePlatformSpecific(final KeyModifier keyModifier) {
        return switch (keyModifier) {
            case CONTROL -> net.neoforged.neoforge.client.settings.KeyModifier.CONTROL;
            case SHIFT -> net.neoforged.neoforge.client.settings.KeyModifier.SHIFT;
            case ALT -> net.neoforged.neoforge.client.settings.KeyModifier.ALT;
        };
    }
}
