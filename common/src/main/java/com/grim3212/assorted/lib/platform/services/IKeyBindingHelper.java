package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.client.key.IKeyConflictHelper;
import com.grim3212.assorted.lib.client.key.KeyModifier;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public interface IKeyBindingHelper {
    /**
     * Registers a new key mapping to the system.
     *
     * @param mapping The new key mapping to register.
     */
    void register(KeyMapping mapping);

    /**
     * The conflict context which detects when a GUI is open.
     *
     * @return The conflict context.
     */
    IKeyConflictHelper getGuiKeyConflictContext();

    /**
     * The conflict context which detects when a GUI is not open.
     *
     * @return The conflict context.
     */
    IKeyConflictHelper getInGameKeyConflictContext();

    /**
     * Creates a new key mapping with the given properties.
     * If the system supports it a conflict context is registered as well.
     *
     * @param translationKey      The translation key for the key mapping.
     * @param keyConflictContext  The optional key conflict context to apply.
     * @param inputType           The input type for the key mapping.
     * @param key                 The default configured key.
     * @param groupTranslationKey The translation key for the group that the key belongs to.
     * @return The new key mapping.
     */
    KeyMapping createNew(String translationKey, IKeyConflictHelper keyConflictContext, InputConstants.Type inputType, int key, String groupTranslationKey);

    /**
     * Creates a new key mapping with the given properties.
     * If the system supports it a conflict context is registered as well.
     *
     * @param translationKey      The translation key for the key mapping.
     * @param keyConflictContext  The optional key conflict context to apply.
     * @param keyModifier         The key modifier for the default key.
     * @param inputType           The input type for the key mapping.
     * @param key                 The default configured key.
     * @param groupTranslationKey The translation key for the group that the key belongs to.
     * @return The new key mapping.
     */
    KeyMapping createNew(String translationKey, IKeyConflictHelper keyConflictContext, KeyModifier keyModifier, InputConstants.Type inputType, int key, String groupTranslationKey);

    /**
     * Checks if the key conflict context of the key mapping is active or not.
     *
     * @param keybinding The key mapping to check.
     * @return True when the conflict context is active, false when not.
     */
    boolean isKeyConflictOfActive(KeyMapping keybinding);

    /**
     * Checks if the key modifier of the key mapping is active or not.
     *
     * @param keybinding The key mapping to check.
     * @return True when the modifier is active, false when not.
     */
    boolean isKeyModifierActive(KeyMapping keybinding);
}
