package com.grim3212.assorted.lib.config;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a builder for the current platform.
 */
public interface IConfigurationBuilder {

    /**
     * Defines a new boolean property with the given key and default value.
     *
     * @param key          The key to use.
     * @param defaultValue The default value.
     * @return The value provider.
     */
    Supplier<Boolean> defineBoolean(String key, boolean defaultValue, String comment);

    /**
     * Defines a new list property with the given key and default value.
     *
     * @param key           The key to use.
     * @param defaultValue  The default value.
     * @param containedType The type of the values in the list.
     * @param <T>           The type contained in the list.
     * @return The value provider.
     */
    <T> Supplier<List<? extends T>> defineList(String key, List<T> defaultValue, final Class<T> containedType, String comment);

    /**
     * Defines a new string property with the given key and default value.
     *
     * @param key          The key to use.
     * @param defaultValue The default value.
     * @return The value provider.
     */
    Supplier<String> defineString(String key, String defaultValue, String comment);

    /**
     * Defines a new long property with the given key and default value.
     *
     * @param key          The key to use.
     * @param defaultValue The default value.
     * @param minValue     The minimal value.
     * @param maxValue     The maximal value.
     * @return The value provider.
     */
    Supplier<Long> defineLong(String key, long defaultValue, long minValue, long maxValue, String comment);

    /**
     * Defines a new integer property with the given key and default value.
     *
     * @param key          The key to use.
     * @param defaultValue The default value.
     * @param minValue     The minimal value.
     * @param maxValue     The maximal value.
     * @return The value provider.
     */
    Supplier<Integer> defineInteger(String key, int defaultValue, int minValue, int maxValue, String comment);

    /**
     * Defines a new double property with the given key and default value.
     *
     * @param key          The key to use.
     * @param defaultValue The default value.
     * @param minValue     The minimal value.
     * @param maxValue     The maximal value.
     * @return The value provider.
     */
    Supplier<Double> defineDouble(String key, double defaultValue, double minValue, double maxValue, String comment);

    /**
     * Defines a new enum based property with the given key and default value.
     *
     * @param key          The key to use.
     * @param defaultValue The default value.
     * @param <T>          The type of the enu,
     * @return The value provider.
     */
    <T extends Enum<T>> Supplier<T> defineEnum(String key, T defaultValue, String comment);

    /**
     * Finalizes the builder and sets up the configuration.
     */
    void setup();

}
