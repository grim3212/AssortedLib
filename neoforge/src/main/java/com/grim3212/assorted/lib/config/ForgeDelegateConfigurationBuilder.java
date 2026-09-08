package com.grim3212.assorted.lib.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeDelegateConfigurationBuilder implements IConfigurationBuilder {

    private final Consumer<ForgeConfigSpec> specConsumer;
    private final ForgeConfigSpec.Builder builder;

    public ForgeDelegateConfigurationBuilder(final Consumer<ForgeConfigSpec> specConsumer) {
        this.specConsumer = specConsumer;
        builder = new ForgeConfigSpec.Builder();
    }

    @Override
    public Supplier<Boolean> defineBoolean(final String key, final boolean defaultValue, final String comment) {
        builder.comment(comment);
        return builder.define(key, defaultValue)::get;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(final String key, final List<T> defaultValue, final Class<T> containedType, final String comment) {
        builder.comment(comment);
        return builder.defineList(key, defaultValue, t -> true)::get;
    }

    @Override
    public Supplier<String> defineString(final String key, final String defaultValue, final String comment) {
        builder.comment(comment);
        return builder.define(key, defaultValue)::get;
    }

    @Override
    public Supplier<Long> defineLong(final String key, final long defaultValue, final long minValue, final long maxValue, final String comment) {
        builder.comment(comment);
        return builder.defineInRange(key, defaultValue, minValue, maxValue)::get;
    }

    @Override
    public Supplier<Integer> defineInteger(final String key, final int defaultValue, final int minValue, final int maxValue, final String comment) {
        builder.comment(comment);
        return builder.defineInRange(key, defaultValue, minValue, maxValue)::get;
    }

    @Override
    public Supplier<Double> defineDouble(final String key, final double defaultValue, final double minValue, final double maxValue, final String comment) {
        builder.comment(comment);
        return builder.defineInRange(key, defaultValue, minValue, maxValue)::get;
    }

    @Override
    public <T extends Enum<T>> Supplier<T> defineEnum(final String key, final T defaultValue, final String comment) {
        builder.comment(comment);
        return builder.defineEnum(key, defaultValue)::get;
    }

    @Override
    public void setup() {
        specConsumer.accept(builder.build());
    }
}
