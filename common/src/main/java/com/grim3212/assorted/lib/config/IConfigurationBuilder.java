package com.grim3212.assorted.lib.config;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a builder for the current platform.
 */
public interface IConfigurationBuilder {

    /** Defines a boolean property with the given key and default value. */
    Supplier<Boolean> defineBoolean(String key, boolean defaultValue, String comment);

    /** Defines a list property whose elements are of {@code containedType}. */
    <T> Supplier<List<? extends T>> defineList(String key, List<T> defaultValue, final Class<T> containedType, String comment);

    /** Defines a string property with the given key and default value. */
    Supplier<String> defineString(String key, String defaultValue, String comment);

    /** Defines a long property between {@code minValue} and {@code maxValue}. */
    Supplier<Long> defineLong(String key, long defaultValue, long minValue, long maxValue, String comment);

    /** Defines an integer property between {@code minValue} and {@code maxValue}. */
    Supplier<Integer> defineInteger(String key, int defaultValue, int minValue, int maxValue, String comment);

    /** Defines a double property between {@code minValue} and {@code maxValue}. */
    Supplier<Double> defineDouble(String key, double defaultValue, double minValue, double maxValue, String comment);

    /** Defines an enum property with the given key and default value. */
    <T extends Enum<T>> Supplier<T> defineEnum(String key, T defaultValue, String comment);

    /**
     * Finalizes the builder and sets up the configuration.
     */
    void setup();

}
