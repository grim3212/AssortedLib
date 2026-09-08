package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.client.key.ForgeKeyConflictContextPlatformDelegate;
import com.grim3212.assorted.lib.client.key.IKeyConflictHelper;
import com.grim3212.assorted.lib.client.key.KeyModifier;
import com.grim3212.assorted.lib.client.key.PlatformKeyConflictContextForgeDelegate;
import com.grim3212.assorted.lib.platform.services.IKeyBindingHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ForgeKeyBindingHelper implements IKeyBindingHelper {

    // TODO(26.2): a key mapping's group is no longer a free-form translation key. KeyMapping takes a
    //  KeyMapping.Category, which is an Identifier that has to be registered through
    //  RegisterKeyMappingsEvent#registerCategory and whose label is derived from that id
    //  ("key.category.<namespace>.<path>"). The group string a caller passes is therefore turned into
    //  a category id here rather than used as the label, so the translation key of an existing group
    //  changes. Categories are cached so the same group string always maps to the same category.
    private static final Map<String, KeyMapping.Category> CATEGORIES = new ConcurrentHashMap<>();

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
            final String translationKey, final IKeyConflictHelper keyConflictContext, final InputConstants.Type inputType, final int key, final String groupTranslationKey) {
        return new KeyMapping(
                translationKey,
                new PlatformKeyConflictContextForgeDelegate(keyConflictContext),
                inputType,
                key,
                getCategory(groupTranslationKey)
        );
    }

    @Override
    public KeyMapping createNew(
            final String translationKey,
            final IKeyConflictHelper keyConflictContext,
            final KeyModifier keyModifier,
            final InputConstants.Type inputType,
            final int key,
            final String groupTranslationKey) {
        return new KeyMapping(
                translationKey,
                new PlatformKeyConflictContextForgeDelegate(keyConflictContext),
                makePlatformSpecific(keyModifier),
                inputType,
                key,
                getCategory(groupTranslationKey)
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
     * The category for a group translation key. Categories are not registered here; every mapping
     * carries its own, and {@link ForgeClientHelper} registers the distinct ones it sees when
     * {@code RegisterKeyMappingsEvent} fires.
     */
    public static KeyMapping.Category getCategory(final String groupTranslationKey) {
        return CATEGORIES.computeIfAbsent(groupTranslationKey, key -> new KeyMapping.Category(Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, toPath(key))));
    }

    private static String toPath(final String groupTranslationKey) {
        final String path = groupTranslationKey.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_./-]", "_");
        return path.isEmpty() ? "misc" : path;
    }

    private static net.neoforged.neoforge.client.settings.KeyModifier makePlatformSpecific(final KeyModifier keyModifier) {
        return switch (keyModifier) {
            case CONTROL -> net.neoforged.neoforge.client.settings.KeyModifier.CONTROL;
            case SHIFT -> net.neoforged.neoforge.client.settings.KeyModifier.SHIFT;
            case ALT -> net.neoforged.neoforge.client.settings.KeyModifier.ALT;
        };
    }
}
