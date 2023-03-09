package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.client.key.ForgeKeyConflictContextPlatformDelegate;
import com.grim3212.assorted.lib.client.key.IKeyConflictHelper;
import com.grim3212.assorted.lib.client.key.KeyModifier;
import com.grim3212.assorted.lib.client.key.PlatformKeyConflictContextForgeDelegate;
import com.grim3212.assorted.lib.platform.services.IKeyBindingHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

import java.util.ArrayList;
import java.util.List;

public class ForgeKeyBindingHelper implements IKeyBindingHelper {
    private static final List<KeyMapping> registeredKeyMappings = new ArrayList<>();

    public static List<KeyMapping> getRegisteredKeyMappings() {
        return registeredKeyMappings;
    }

    @Override
    public void register(final KeyMapping mapping) {
        registeredKeyMappings.add(mapping);
    }

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
                groupTranslationKey
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
                groupTranslationKey
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

    private static net.minecraftforge.client.settings.KeyModifier makePlatformSpecific(final KeyModifier keyModifier) {
        return switch (keyModifier) {
            case CONTROL -> net.minecraftforge.client.settings.KeyModifier.CONTROL;
            case SHIFT -> net.minecraftforge.client.settings.KeyModifier.SHIFT;
            case ALT -> net.minecraftforge.client.settings.KeyModifier.ALT;
        };
    }
}
