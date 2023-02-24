package com.grim3212.assorted.lib.config;

import java.util.function.Supplier;

public class ConfigOption<T> {

    private final String name;
    private final OptionType type;
    private final T defaultValue;
    private final String comment;
    private Supplier<T> currentValue;

    public ConfigOption(String name, OptionType type, T defaultValue, String comment) {
        this.defaultValue = defaultValue;
        this.type = type;
        this.name = name;
        this.comment = comment;
    }

    public String getName() {
        return this.name;
    }

    public OptionType getType() {
        return type;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public String getComment() {
        return this.comment;
    }

    public void setValueSupplier(Supplier<?> currentValue) {
        this.currentValue = (Supplier<T>) currentValue;
    }

    public T getValue() {
        return this.currentValue == null ? this.getDefaultValue() : this.currentValue.get();
    }

    public static abstract class ConfigOptionMinMax<T extends Number> extends ConfigOption<T> {
        private final T min;
        private final T max;

        public ConfigOptionMinMax(String name, OptionType type, T defaultValue, String comment, T min, T max) {
            super(name, type, defaultValue, comment);
            this.min = min;
            this.max = max;
        }

        public T getMin() {
            return min;
        }

        public T getMax() {
            return max;
        }

        public Class<T> getClazz() {
            return (Class<T>) this.getDefaultValue().getClass();
        }
    }

    public static class ConfigOptionInteger extends ConfigOptionMinMax<Integer> {

        public ConfigOptionInteger(String name, Integer defaultValue, String comment, Integer min, Integer max) {
            super(name, OptionType.INTEGER, defaultValue, comment, min, max);
        }
    }

    public static class ConfigOptionFloat extends ConfigOptionMinMax<Float> {

        public ConfigOptionFloat(String name, Float defaultValue, String comment, Float min, Float max) {
            super(name, OptionType.FLOAT, defaultValue, comment, min, max);
        }
    }

    public enum OptionType {
        BOOLEAN,
        INTEGER,
        FLOAT,
        STRING
    }
}
