package com.grim3212.assorted.lib.platform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.config.*;
import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.dist.DistExecutor;
import com.grim3212.assorted.lib.platform.services.IConfigHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FabricConfigHelper implements IConfigHelper {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final ResourceLocation CONFIG_SYNC_CHANNEL_ID = new ResourceLocation(LibConstants.MOD_ID, "config_sync");

    private static final Map<String, FabricConfigurationSpec> syncedSources = Maps.newHashMap();
    private static final List<FabricConfigurationSpec> noneSyncedSources = Lists.newArrayList();

    public static void init() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FabricConfigurationNetworkingUtils.registerNetworkingChannel(
                CONFIG_SYNC_CHANNEL_ID,
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

        final String payload = GSON.toJson(targetObject);
        final FriendlyByteBuf buffer = PacketByteBufs.create();

        buffer.writeUtf(payload);

        ServerPlayNetworking.send(serverPlayer, CONFIG_SYNC_CHANNEL_ID, buffer);
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

    private JsonObject loadLocalConfig(final String name) {
        try {
            final File configurationDirectory = FabricLoader.getInstance().getConfigDirectory();
            final Path configPath = Path.of(configurationDirectory.getAbsolutePath(), name + ".json");

            final FileReader fileReader = new FileReader(configPath.toAbsolutePath().toFile().getAbsolutePath());

            final JsonElement containedElement = GSON.fromJson(fileReader, JsonElement.class);
            if (!containedElement.isJsonObject())
                throw new IllegalStateException("Config file: " + name + " is not a json object!");

            fileReader.close();

            return containedElement.getAsJsonObject();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to open and read configuration file: " + name, e);
        }
    }

    private boolean doesLocalConfigExist(final String name) {
        final File configurationDirectory = FabricLoader.getInstance().getConfigDirectory();
        final Path configPath = Path.of(configurationDirectory.getAbsolutePath(), name + ".json");
        return Files.exists(configPath);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveLocalConfig(final String name, final JsonObject config) {
        try {
            final File configurationDirectory = FabricLoader.getInstance().getConfigDirectory();
            final Path configPath = Path.of(configurationDirectory.getAbsolutePath(), name + ".json");
            if (Files.exists(configPath))
                Files.delete(configPath);

            configPath.toFile().getParentFile().mkdirs();
            Files.createFile(configPath);

            final FileWriter fileWriter = new FileWriter(configPath.toAbsolutePath().toFile().getAbsolutePath());
            GSON.toJson(config, fileWriter);

            fileWriter.close();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to open and read configuration file: " + name, e);
        }
    }
}
