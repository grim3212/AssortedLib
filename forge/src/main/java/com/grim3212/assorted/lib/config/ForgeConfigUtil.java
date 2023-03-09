package com.grim3212.assorted.lib.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ForgeConfigUtil {

    public static ForgeConfigSpec getConfigSpec(ConfigBuilder groupBuilder) {
        return new ForgeConfigSpec.Builder().configure(x -> new ForgeConfig(x, groupBuilder)).getRight();
    }

    private static class ForgeConfig {
        public ForgeConfig(ForgeConfigSpec.Builder builder, ConfigBuilder groupBuilder) {
            for (ConfigGroup group : groupBuilder.getGroups()) {
                builder.push(group.groupName);
                for (ConfigOption<?> option : group.getOptions()) {
                    ForgeConfigSpec.Builder optionBuilder = builder.comment(option.getComment());
                    ForgeConfigSpec.ConfigValue<?> value;
                    if (option instanceof ConfigOption.ConfigOptionMinMax<?> minMax) {
                        if (minMax.getType() == ConfigOption.OptionType.INTEGER) {
                            value = optionBuilder.defineInRange(minMax.getName(), (int) minMax.getDefaultValue(), (int) minMax.getMin(), (int) minMax.getMax());
                        } else {
                            value = optionBuilder.defineInRange(minMax.getName(), (double) minMax.getDefaultValue(), (double) minMax.getMin(), (double) minMax.getMax());
                        }
                    } else {
                        value = optionBuilder.define(option.getName(), option.getDefaultValue());
                    }
                    option.setValueSupplier(() -> value.get());
                }
                // Only support 1 level deep subgroups
                for (ConfigGroup subGroup : group.getSubGroups()) {
                    builder.push(subGroup.groupName);
                    for (ConfigOption<?> option : subGroup.getOptions()) {
                        ForgeConfigSpec.Builder optionBuilder = builder.comment(option.getComment());
                        ForgeConfigSpec.ConfigValue<?> value;
                        if (option instanceof ConfigOption.ConfigOptionMinMax<?> minMax) {
                            if (minMax.getType() == ConfigOption.OptionType.INTEGER) {
                                value = optionBuilder.defineInRange(minMax.getName(), (int) minMax.getDefaultValue(), (int) minMax.getMin(), (int) minMax.getMax());
                            } else {
                                value = optionBuilder.defineInRange(minMax.getName(), (double) minMax.getDefaultValue(), (double) minMax.getMin(), (double) minMax.getMax());
                            }
                        } else {
                            value = optionBuilder.define(option.getName(), option.getDefaultValue());
                        }
                        option.setValueSupplier(() -> value.get());
                    }
                    builder.pop();
                }
                builder.pop();
            }
        }
    }
}
