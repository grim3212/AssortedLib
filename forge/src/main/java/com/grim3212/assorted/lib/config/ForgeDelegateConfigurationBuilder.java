package com.grim3212.assorted.lib.config;

import com.google.common.collect.Sets;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeDelegateConfigurationBuilder implements IConfigurationBuilder {

    private final Consumer<ForgeConfigSpec> specConsumer;
    private final Consumer<String> keyConsumer;
    private final ForgeConfigSpec.Builder builder;

    private final Set<String> keys = Sets.newConcurrentHashSet();

    public ForgeDelegateConfigurationBuilder(final Consumer<ForgeConfigSpec> specConsumer, final Consumer<String> keyConsumer) {
        this.specConsumer = specConsumer;
        this.keyConsumer = keyConsumer;
        builder = new ForgeConfigSpec.Builder();
    }

    @Override
    public Supplier<Boolean> defineBoolean(final String key, final boolean defaultValue) {
        keys.add(key);
        builder.comment(key);
        return builder.define(key, defaultValue)::get;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(final String key, final List<T> defaultValue, final Class<T> containedType) {
        keys.add(key);
        builder.comment(key);
        return builder.defineList(key, defaultValue, t -> true)::get;
    }

    @Override
    public Supplier<String> defineString(final String key, final String defaultValue) {
        keys.add(key);
        builder.comment(key);
        return builder.define(key, defaultValue)::get;
    }

    @Override
    public Supplier<Long> defineLong(final String key, final long defaultValue, final long minValue, final long maxValue) {
        keys.add(key);
        builder.comment(key);
        return builder.defineInRange(key, defaultValue, minValue, maxValue)::get;
    }

    @Override
    public Supplier<Integer> defineInteger(final String key, final int defaultValue, final int minValue, final int maxValue) {
        keys.add(key);
        builder.comment(key);
        return builder.defineInRange(key, defaultValue, minValue, maxValue)::get;
    }

    @Override
    public Supplier<Double> defineDouble(final String key, final double defaultValue, final double minValue, final double maxValue) {
        keys.add(key);
        builder.comment(key);
        return builder.defineInRange(key, defaultValue, minValue, maxValue)::get;
    }

    @Override
    public <T extends Enum<T>> Supplier<T> defineEnum(final String key, final T defaultValue) {
        keys.add(key);
        builder.comment(key);
        return builder.defineEnum(key, defaultValue)::get;
    }

    @Override
    public void setup() {
        keys.forEach(keyConsumer);
        specConsumer.accept(builder.build());
    }
}
