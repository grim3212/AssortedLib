package com.grim3212.assorted.lib.platform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.config.*;
import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.dist.DistExecutor;
import com.grim3212.assorted.lib.platform.services.IConfigHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FabricConfigHelper implements IConfigHelper {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, FabricConfigurationSpec> syncedSources = Maps.newHashMap();
    private static final List<FabricConfigurationSpec> noneSyncedSources = Lists.newArrayList();

    public static void init() {
        // The payload type has to be declared on both sides before any connection is opened; only the
        // client side actually installs a receiver for it.
        FabricConfigurationNetworkingUtils.registerPayloads();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FabricConfigurationNetworkingUtils.registerNetworkingChannel(
                FabricConfigurationNetworkingUtils.CONFIG_SYNC_CHANNEL_ID,
                GSON,
                () -> syncedSources
        ));

        ServerPlayConnectionEvents.JOIN.register((listener, packetSender, minecraftServer) -> syncTo(listener.getPlayer()));
    }

    public static void syncTo(final ServerPlayer serverPlayer) {
        final JsonObject targetObject = new JsonObject();
        syncedSources.forEach((key, spec) -> {
            final JsonObject specObject = spec.getSource().getConfig();
            targetObject.add(key, specObject);
        });

        FabricConfigurationNetworkingUtils.sendTo(serverPlayer, GSON.toJson(targetObject));
    }

    @Override
    public IConfigurationBuilder createBuilder(
            final ConfigurationType type, final String name) {

        final JsonObject localConfig = doesLocalConfigExist(name) ? loadLocalConfig(name) : new JsonObject();
        final FabricConfigurationSource source = new FabricConfigurationSource(name, localConfig);

        return new FabricConfigurationBuilder(source, fabricConfigurationSpec -> {
            if (type == ConfigurationType.SYNCED) {
                syncedSources.put(name, fabricConfigurationSpec);
            } else {
                noneSyncedSources.add(fabricConfigurationSpec);
            }

            fabricConfigurationSpec.forceGetAll();
            fabricConfigurationSpec.writeAll();

            saveLocalConfig(name, source.getConfig());
        });
    }

    private Path configPath(final String name) {
        return FabricLoader.getInstance().getConfigDir().resolve(name + ".json");
    }

    private JsonObject loadLocalConfig(final String name) {
        final Path configPath = configPath(name);

        try (final Reader reader = Files.newBufferedReader(configPath)) {
            final JsonElement containedElement = GSON.fromJson(reader, JsonElement.class);
            if (!containedElement.isJsonObject())
                throw new IllegalStateException("Config file: " + name + " is not a json object!");

            return containedElement.getAsJsonObject();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to open and read configuration file: " + name, e);
        }
    }

    private boolean doesLocalConfigExist(final String name) {
        return Files.exists(configPath(name));
    }

    private void saveLocalConfig(final String name, final JsonObject config) {
        final Path configPath = configPath(name);

        try {
            Files.createDirectories(configPath.getParent());

            try (final Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to open and read configuration file: " + name, e);
        }
    }
}
