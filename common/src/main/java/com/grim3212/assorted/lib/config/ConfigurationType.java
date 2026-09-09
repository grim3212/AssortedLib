package com.grim3212.assorted.lib.config;

/**
 * Indicates the configuration type used by the configuration that is about to be build.
 */
public enum ConfigurationType {
    /**
     * Indicates that the configuration is a client configuration.
     * The server will not load this configuration, nor is it synced to the client.
     */
    CLIENT_ONLY,

    /**
     * Indicates that the configuration is common to both distributions.
     * Both the server and the client will load the configuration from disk, however the values will not be synced from the server to the client.
     */
    NOT_SYNCED,

    /**
     * Indicates that the configuration is common to both distributions, and has influence on logical decisions.
     * Both the server and the client will load the configuration from disk, and the server will sync its settings to the client when it joins.
     */
    SYNCED,

    /**
     * Indicates that the configuration is common to both distributions and has to be readable
     * before the registries are populated, because its values decide what gets registered or what
     * the registered objects are built from.
     * <p>
     * This exists because item statistics stopped being questions an item answers on demand and
     * became data components fixed when the item is constructed. A mod that drives those from
     * configuration has to have the file in hand during registration, and an ordinary
     * {@link #NOT_SYNCED} configuration is not loaded until well after that - reading one during
     * {@code RegisterEvent} throws outright on NeoForge.
     * <p>
     * The trade is that these values cannot be reloaded while the game runs and are never synced,
     * which is inherent: they have already been baked into the registered objects by the time
     * anything could reload them.
     */
    NEEDED_AT_REGISTRATION;
}
