package com.grim3212.assorted.lib.config;

import com.grim3212.assorted.lib.LibConstants;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigLeaf;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;

import javax.annotation.Nullable;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.function.Supplier;

public class FabricConfigUtil {

    private static void writeDefaultConfig(String modId, ConfigTree config, Path path, JanksonValueSerializer serializer) {
        try (OutputStream s = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
            FiberSerialization.serialize(config, s, serializer);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) {
            LibConstants.LOG.error(modId, "Error writing default config", e);
        }
    }

    private static void setupConfig(String modId, ConfigTree config, Path p, JanksonValueSerializer serializer) {
        writeDefaultConfig(modId, config, p, serializer);

        try (InputStream s = new BufferedInputStream(Files.newInputStream(p, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
            FiberSerialization.deserialize(config, s, serializer);
        } catch (IOException | ValueDeserializationException e) {
            LibConstants.LOG.error(modId, "Error loading config from {}", p, e);
        }
    }

    public static void setupCommon(String modId, @Nullable ConfigBuilder commonConfig) {
        setup(modId, commonConfig, null);
    }

    public static void setupClient(String modId, @Nullable ConfigBuilder clientConfig) {
        setup(modId, null, clientConfig);
    }

    public static void setup(String modId, @Nullable ConfigBuilder commonConfig, @Nullable ConfigBuilder clientConfig) {
        try {
            Files.createDirectory(Paths.get("config"));
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) {
            LibConstants.LOG.warn(modId, "Failed to make config dir", e);
        }

        JanksonValueSerializer serializer = new JanksonValueSerializer(false);
        if (commonConfig != null) {
            setupConfig(modId, processBranch(ConfigTree.builder(), commonConfig), Paths.get("config", modId + "-common.json5"), serializer);
        }

        if (clientConfig != null) {
            setupConfig(modId, processBranch(ConfigTree.builder(), clientConfig), Paths.get("config", modId + "-client.json5"), serializer);
        }
    }

    public static ConfigBranch processBranch(ConfigTreeBuilder builder, ConfigBuilder groupBuilder) {
        for (ConfigGroup group : groupBuilder.getGroups()) {
            ConfigTreeBuilder groupFork = builder.fork(group.groupName);
            for (ConfigOption<?> option : group.getOptions()) {
                var optionType = mapOption(option);

                groupFork
                        .beginValue(option.getName(), optionType, option.getDefaultValue())
                        .withComment(option.getComment())
                        .finishValue(x -> option.setValueSupplier(getValueSuppler(optionType, x)));
            }
            groupFork.finishBranch();
        }

        return builder.build();
    }


    private static <T> Supplier<?> getValueSuppler(ConfigType<Object, ?, ? extends SerializableType<?>> optionType, ConfigLeaf<T> leaf) {
        if (optionType.getRuntimeType().equals(Integer.class)) {
            return () -> ((BigDecimal) leaf.getValue()).intValue();
        } else if (optionType.getRuntimeType().equals(Float.class)) {
            return () -> ((BigDecimal) leaf.getValue()).floatValue();
        } else {
            return () -> leaf.getValue();
        }
    }

    @Nullable
    private static <T> ConfigType<T, ?, ?> mapOption(ConfigOption option) {
        boolean addMinMax = option instanceof ConfigOption.ConfigOptionMinMax<?>;

        switch (option.getType()) {
            case BOOLEAN -> {
                return (ConfigType<T, ?, ?>) ConfigTypes.BOOLEAN;
            }
            case INTEGER -> {
                return (ConfigType<T, ?, ?>) (addMinMax ? ConfigTypes.NATURAL.withMinimum(((ConfigOption.ConfigOptionMinMax<Integer>) option).getMin()).withMaximum(((ConfigOption.ConfigOptionMinMax<Integer>) option).getMax()) : ConfigTypes.NATURAL);
            }
            case FLOAT -> {
                return (ConfigType<T, ?, ?>) (addMinMax ? ConfigTypes.FLOAT.withMinimum(((ConfigOption.ConfigOptionMinMax<Float>) option).getMin()).withMaximum(((ConfigOption.ConfigOptionMinMax<Float>) option).getMax()) : ConfigTypes.FLOAT);
            }
            case STRING -> {
                return (ConfigType<T, ?, ?>) ConfigTypes.STRING;
            }
        }
        return null;
    }

    public interface IConfig {

        // Add all of the fields and config options to the tree
        ConfigTree configure(ConfigTreeBuilder builder);
    }
}
